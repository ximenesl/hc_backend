package com.gerenciamento.certificado.dto;

public class CursoResponse {
    private Long id;
    private String nome;
    private Integer horasTotais;
    private Long coordenadorId;
    private String coordenadorNome;
    private String coordenadorEmail;
    private java.time.LocalDateTime dataCriacao;
    private Long studentsCount;

    public CursoResponse() {}

    public CursoResponse(Long id, String nome, Integer horasTotais) {
        this.id = id;
        this.nome = nome;
        this.horasTotais = horasTotais;
    }

    public CursoResponse(Long id, String nome, Integer horasTotais, Long coordenadorId, String coordenadorNome, String coordenadorEmail, java.time.LocalDateTime dataCriacao, Long studentsCount) {
        this.id = id;
        this.nome = nome;
        this.horasTotais = horasTotais;
        this.coordenadorId = coordenadorId;
        this.coordenadorNome = coordenadorNome;
        this.coordenadorEmail = coordenadorEmail;
        this.dataCriacao = dataCriacao;
        this.studentsCount = studentsCount;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public Integer getHorasTotais() { return horasTotais; }
    public void setHorasTotais(Integer horasTotais) { this.horasTotais = horasTotais; }
    public Long getCoordenadorId() { return coordenadorId; }
    public void setCoordenadorId(Long coordenadorId) { this.coordenadorId = coordenadorId; }
    public String getCoordenadorNome() { return coordenadorNome; }
    public void setCoordenadorNome(String coordenadorNome) { this.coordenadorNome = coordenadorNome; }
    public String getCoordenadorEmail() { return coordenadorEmail; }
    public void setCoordenadorEmail(String coordenadorEmail) { this.coordenadorEmail = coordenadorEmail; }
    public java.time.LocalDateTime getDataCriacao() { return dataCriacao; }
    public void setDataCriacao(java.time.LocalDateTime dataCriacao) { this.dataCriacao = dataCriacao; }
    public Long getStudentsCount() { return studentsCount; }
    public void setStudentsCount(Long studentsCount) { this.studentsCount = studentsCount; }
}

