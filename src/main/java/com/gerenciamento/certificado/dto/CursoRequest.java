package com.gerenciamento.certificado.dto;

import jakarta.validation.constraints.NotBlank;

public class CursoRequest {
    @NotBlank(message = "O nome do curso é obrigatório")
    private String nome;

    public CursoRequest() {}

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
}
