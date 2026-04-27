package com.gerenciamento.certificado.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class TurmaRequest {

    @NotBlank(message = "O nome da turma é obrigatório")
    private String nome;

    @NotNull(message = "O ID do curso é obrigatório")
    private Long cursoId;

    public TurmaRequest() {}

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public Long getCursoId() { return cursoId; }
    public void setCursoId(Long cursoId) { this.cursoId = cursoId; }
}
