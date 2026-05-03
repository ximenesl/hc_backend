package com.gerenciamento.certificado.dto;

public class RegraResponse {
    private Long id;
    private Long courseId;
    private String type;
    private String grupo;
    private String descricao;
    private String aproveitamento;
    private String requisito;
    private Boolean ativo;

    public RegraResponse() {}

    public RegraResponse(Long id, Long courseId, String type, String grupo, String descricao, String aproveitamento, String requisito) {
        this.id = id;
        this.courseId = courseId;
        this.type = type;
        this.grupo = grupo;
        this.descricao = descricao;
        this.aproveitamento = aproveitamento;
        this.requisito = requisito;
        this.ativo = true;
    }

    public RegraResponse(Long id, Long courseId, String type, String grupo, String descricao, String aproveitamento, String requisito, Boolean ativo) {
        this.id = id;
        this.courseId = courseId;
        this.type = type;
        this.grupo = grupo;
        this.descricao = descricao;
        this.aproveitamento = aproveitamento;
        this.requisito = requisito;
        this.ativo = ativo;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getCourseId() { return courseId; }
    public void setCourseId(Long courseId) { this.courseId = courseId; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
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
