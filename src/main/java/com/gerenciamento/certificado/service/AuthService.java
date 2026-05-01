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
import org.springframework.security.crypto.password.PasswordEncoder;


@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    @org.springframework.beans.factory.annotation.Value("${app.frontend-url}")
    private String frontendUrl;

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

        java.util.List<Long> cursoIds = user.getCursos().stream()
                .map(com.gerenciamento.certificado.entity.Curso::getId)
                .collect(java.util.stream.Collectors.toList());

        return TokenResponse.builder()
                .token(token)
                .id(user.getId())
                .nome(user.getNome())
                .role(user.getRole().name())
                .cursoIds(cursoIds)
                .build();

    }

    private String generateRandomPassword() {
        java.security.SecureRandom random = new java.security.SecureRandom();
        int num = random.nextInt(100000000);
        return String.format("%08d", num);
    }

    public void forgotPassword(ForgotPasswordRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        String novaSenha = generateRandomPassword();
        user.setSenha(passwordEncoder.encode(novaSenha));
        
        userRepository.save(user);

        String html = "<p>Olá " + user.getNome() + ",</p>"
                + "<p>Você solicitou a recuperação de senha.</p>"
                + "<p>Sua nova senha de acesso é: <strong style=\"font-size: 20px;\">" + novaSenha + "</strong></p>"
                + "<p>Recomendamos que você altere sua senha clicando <a href=\"" + frontendUrl + "/redefine-password\">aqui</a>.</p>";

        emailService.enviarEmail(user.getEmail(), "Recuperação de Senha - Sistema de Certificados", html);

    }

    public void changePassword(com.gerenciamento.certificado.dto.ChangePasswordRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        if (!passwordEncoder.matches(request.getSenhaAntiga(), user.getSenha())) {
            throw new IllegalArgumentException("Senha antiga incorreta.");
        }

        user.setSenha(passwordEncoder.encode(request.getSenhaNova()));
        userRepository.save(user);
    }
}

