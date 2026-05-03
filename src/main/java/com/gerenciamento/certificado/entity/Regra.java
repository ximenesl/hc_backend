package com.gerenciamento.certificado.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "regras")
public class Regra {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "curso_id", nullable = false)
    private Curso curso;

    @Column(nullable = false)
    private String tipo; // Ensino, Pesquisa, Extensão

    @Column(nullable = false)
    private String grupo;

    @Column(nullable = false)
    private String descricao;

    @Column(nullable = false)
    private String aproveitamento;

    @Column(nullable = false)
    private String requisito;

    @Column(nullable = false)
    private Boolean ativo = true;

    public Regra() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Curso getCurso() { return curso; }
    public void setCurso(Curso curso) { this.curso = curso; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public String getGrupo() { return grupo; }
    public void setGrupo(String grupo) { this.grupo = grupo; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public String getAproveitamento() { return aproveitamento; }
    public void setAproveitamento(String aproveitamento) { this.aproveitamento = aproveitamento; }
    public String getRequisito() { return requisito; }
    public void setRequisito(String requisito) { this.requisito = requisito; }
    public Boolean getAtivo() { return ativo; }
    public void setAtivo(Boolean ativo) { this.ativo = ativo; }
}
