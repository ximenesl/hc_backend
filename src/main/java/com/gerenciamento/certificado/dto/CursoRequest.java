package com.gerenciamento.certificado.dto;

import jakarta.validation.constraints.NotBlank;

public class CursoRequest {
    @NotBlank(message = "O nome do curso é obrigatório")
    private String nome;

    private Integer horasTotais;

    private Long coordenadorId;

    public CursoRequest() {}

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public Integer getHorasTotais() { return horasTotais; }
    public void setHorasTotais(Integer horasTotais) { this.horasTotais = horasTotais; }
    public Long getCoordenadorId() { return coordenadorId; }
    public void setCoordenadorId(Long coordenadorId) { this.coordenadorId = coordenadorId; }
}

