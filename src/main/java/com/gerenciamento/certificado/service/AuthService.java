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

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    public AuthService(AuthenticationManager authenticationManager, JwtUtil jwtUtil, UserRepository userRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
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
}
