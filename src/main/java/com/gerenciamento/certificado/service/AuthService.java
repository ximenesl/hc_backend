package com.gerenciamento.certificado.service;

import com.gerenciamento.certificado.dto.LoginRequest;
import com.gerenciamento.certificado.dto.TokenResponse;
import com.gerenciamento.certificado.entity.User;
import com.gerenciamento.certificado.repository.UserRepository;
import com.gerenciamento.certificado.security.JwtUtil;
import com.gerenciamento.certificado.exception.ResourceNotFoundException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import com.gerenciamento.certificado.dto.ForgotPasswordRequest;
import com.gerenciamento.certificado.dto.ResetPasswordRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    public AuthService(AuthenticationManager authenticationManager, JwtUtil jwtUtil, UserRepository userRepository, EmailService emailService, PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
        this.emailService = emailService;
        this.passwordEncoder = passwordEncoder;
    }

    public TokenResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getSenha())
        );

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        String token = jwtUtil.generateToken(user.getEmail());

        return TokenResponse.builder()
                .token(token)
                .id(user.getId())
                .nome(user.getNome())
                .role(user.getRole().name())
                .build();
    }

    private String generateRecoveryCode() {
        SecureRandom random = new SecureRandom();
        int num = random.nextInt(1000000);
        return String.format("%06d", num);
    }

    public void forgotPassword(ForgotPasswordRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        String codigo = generateRecoveryCode();
        user.setCodigoRecuperacao(codigo);
        user.setCodigoRecuperacaoExpiraEm(LocalDateTime.now().plusMinutes(15));

        userRepository.save(user);

        String html = "<p>Olá " + user.getNome() + ",</p>"
                + "<p>Você solicitou a recuperação de senha.</p>"
                + "<p>Seu código de verificação é: <strong style=\"font-size: 24px;\">" + codigo + "</strong></p>"
                + "<p>Este código é válido por 15 minutos.</p>";

        emailService.enviarEmail(user.getEmail(), "Código de Recuperação de Senha - Sistema de Certificados", html);
    }

    public void verifyCode(com.gerenciamento.certificado.dto.VerifyCodeRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        if (user.getCodigoRecuperacao() == null || !user.getCodigoRecuperacao().equals(request.getCodigo())) {
            throw new IllegalArgumentException("Código de recuperação inválido.");
        }

        if (user.getCodigoRecuperacaoExpiraEm() == null || user.getCodigoRecuperacaoExpiraEm().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Código de recuperação expirado.");
        }
    }

    public void resetPassword(ResetPasswordRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        if (user.getCodigoRecuperacao() == null || !user.getCodigoRecuperacao().equals(request.getCodigo())) {
            throw new IllegalArgumentException("Código de recuperação inválido.");
        }

        if (user.getCodigoRecuperacaoExpiraEm() == null || user.getCodigoRecuperacaoExpiraEm().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Código de recuperação expirado.");
        }

        user.setSenha(passwordEncoder.encode(request.getNovaSenha()));
        user.setCodigoRecuperacao(null);
        user.setCodigoRecuperacaoExpiraEm(null);

        userRepository.save(user);
    }
}
