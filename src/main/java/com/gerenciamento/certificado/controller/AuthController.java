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
    @Operation(summary = "Solicitar recuperação de senha", description = "Envia um código de 6 dígitos para o email do usuário")
    public ResponseEntity<Void> forgotPassword(@Valid @RequestBody com.gerenciamento.certificado.dto.ForgotPasswordRequest request) {
        authService.forgotPassword(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/reset-password")
    @Operation(summary = "Redefinir senha", description = "Valida o código de 6 dígitos e redefine a senha do usuário")
    public ResponseEntity<Void> resetPassword(@Valid @RequestBody com.gerenciamento.certificado.dto.ResetPasswordRequest request) {
        authService.resetPassword(request);
        return ResponseEntity.ok().build();
    }
}
