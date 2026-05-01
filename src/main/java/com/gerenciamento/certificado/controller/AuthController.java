package com.gerenciamento.certificado.controller;

import com.gerenciamento.certificado.dto.LoginRequest;
import com.gerenciamento.certificado.dto.TokenResponse;
import com.gerenciamento.certificado.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Autenticação", description = "Endpoints para login no sistema")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    @Operation(summary = "Realizar Login", description = "Retorna o token JWT e informações básicas do usuário")
    public ResponseEntity<TokenResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/forgot-password")
    @Operation(summary = "Solicitar recuperação de senha", description = "Gera uma nova senha aleatória e envia para o email do usuário")
    public ResponseEntity<Void> forgotPassword(@Valid @RequestBody com.gerenciamento.certificado.dto.ForgotPasswordRequest request) {
        authService.forgotPassword(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/change-password")
    @Operation(summary = "Alterar senha (usuário logado)", description = "Altera a senha do usuário validando a senha antiga")
    public ResponseEntity<Void> changePassword(@Valid @RequestBody com.gerenciamento.certificado.dto.ChangePasswordRequest request) {
        authService.changePassword(request);
        return ResponseEntity.ok().build();
    }
}

