package com.gerenciamento.certificado.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class RegraRequest {
    @NotNull(message = "O ID do curso é obrigatório")
    private Long cursoId;

    @NotBlank(message = "O tipo é obrigatório")
    private String tipo;

    @NotBlank(message = "O grupo é obrigatório")
    private String grupo;

    @NotBlank(message = "A descrição é obrigatória")
    private String descricao;

    @NotBlank(message = "O aproveitamento é obrigatório")
    private String aproveitamento;

    @NotBlank(message = "O requisito é obrigatório")
    private String requisito;

    public RegraRequest() {}

    public Long getCursoId() { return cursoId; }
    public void setCursoId(Long cursoId) { this.cursoId = cursoId; }
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
}
