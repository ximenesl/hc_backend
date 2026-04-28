package com.gerenciamento.certificado.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "cursos")
public class Curso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String nome;

    @Column(name = "horas_totais")
    private Integer horasTotais;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coordenador_id")
    private User coordenador;

    @Column(name = "data_criacao", updatable = false)
    private java.time.LocalDateTime dataCriacao;

    @PrePersist
    protected void onCreate() {
        dataCriacao = java.time.LocalDateTime.now();
    }

    public Curso() {}

    public Curso(Long id, String nome, Integer horasTotais) {
        this.id = id;
        this.nome = nome;
        this.horasTotais = horasTotais;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public Integer getHorasTotais() { return horasTotais; }
    public void setHorasTotais(Integer horasTotais) { this.horasTotais = horasTotais; }
    public User getCoordenador() { return coordenador; }
    public void setCoordenador(User coordenador) { this.coordenador = coordenador; }

    public java.time.LocalDateTime getDataCriacao() { return dataCriacao; }
    public void setDataCriacao(java.time.LocalDateTime dataCriacao) { this.dataCriacao = dataCriacao; }
}

