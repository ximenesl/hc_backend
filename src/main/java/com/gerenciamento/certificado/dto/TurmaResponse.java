package com.gerenciamento.certificado.dto;

import com.gerenciamento.certificado.entity.Turma;

public class TurmaResponse {
    private Long id;
    private String nome;
    private Long cursoId;

    public TurmaResponse() {}

    public TurmaResponse(Turma turma) {
        this.id = turma.getId();
        this.nome = turma.getNome();
        this.cursoId = turma.getCurso().getId();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public Long getCursoId() { return cursoId; }
    public void setCursoId(Long cursoId) { this.cursoId = cursoId; }
}
