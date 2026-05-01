package com.gerenciamento.certificado.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class ChangePasswordRequest {

    @Email(message = "Email inválido")
    @NotBlank(message = "O email é obrigatório")
    private String email;

    @NotBlank(message = "A senha antiga é obrigatória")
    private String senhaAntiga;

    @NotBlank(message = "A nova senha é obrigatória")
    private String senhaNova;

    public ChangePasswordRequest() {}

    public ChangePasswordRequest(String email, String senhaAntiga, String senhaNova) {
        this.email = email;
        this.senhaAntiga = senhaAntiga;
        this.senhaNova = senhaNova;
    }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getSenhaAntiga() { return senhaAntiga; }
    public void setSenhaAntiga(String senhaAntiga) { this.senhaAntiga = senhaAntiga; }

    public String getSenhaNova() { return senhaNova; }
    public void setSenhaNova(String senhaNova) { this.senhaNova = senhaNova; }
}

