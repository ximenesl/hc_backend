package com.gerenciamento.certificado.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class LoginRequest {
    @NotBlank(message = "O email não pode ser vazio")
    @Email(message = "Email inválido")
    private String email;

    @NotBlank(message = "A senha não pode ser vazia")
    private String senha;

    public LoginRequest() {}

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }
}
