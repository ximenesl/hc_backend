package com.gerenciamento.certificado.dto;

import java.time.LocalDate;

public class CertificadoResponse {
    private Long id;
    private String nome;
    private Integer cargaHoraria;
    private LocalDate dataEmissao;
    private String status;
    private String arquivoUrl;
    private Long alunoId;
    private String alunoNome;
    private String justificativa;
    private Integer horasValidadas;
    private Long regraId;
    private String regraDescricao;

    public CertificadoResponse() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public Integer getCargaHoraria() { return cargaHoraria; }
    public void setCargaHoraria(Integer cargaHoraria) { this.cargaHoraria = cargaHoraria; }
    public LocalDate getDataEmissao() { return dataEmissao; }
    public void setDataEmissao(LocalDate dataEmissao) { this.dataEmissao = dataEmissao; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getArquivoUrl() { return arquivoUrl; }
    public void setArquivoUrl(String arquivoUrl) { this.arquivoUrl = arquivoUrl; }
    public Long getAlunoId() { return alunoId; }
    public void setAlunoId(Long alunoId) { this.alunoId = alunoId; }
    public String getAlunoNome() { return alunoNome; }
    public void setAlunoNome(String alunoNome) { this.alunoNome = alunoNome; }
    public String getJustificativa() { return justificativa; }
    public void setJustificativa(String justificativa) { this.justificativa = justificativa; }
    public Integer getHorasValidadas() { return horasValidadas; }
    public void setHorasValidadas(Integer horasValidadas) { this.horasValidadas = horasValidadas; }
    public Long getRegraId() { return regraId; }
    public void setRegraId(Long regraId) { this.regraId = regraId; }
    public String getRegraDescricao() { return regraDescricao; }
    public void setRegraDescricao(String regraDescricao) { this.regraDescricao = regraDescricao; }

    public static CertificadoResponseBuilder builder() { return new CertificadoResponseBuilder(); }

    public static class CertificadoResponseBuilder {
        private Long id;
        private String nome;
        private Integer cargaHoraria;
        private LocalDate dataEmissao;
        private String status;
        private String arquivoUrl;
        private Long alunoId;
        private String alunoNome;
        private String justificativa;
        private Integer horasValidadas;
        private Long regraId;
        private String regraDescricao;

        public CertificadoResponseBuilder id(Long id) { this.id = id; return this; }
        public CertificadoResponseBuilder nome(String nome) { this.nome = nome; return this; }
        public CertificadoResponseBuilder cargaHoraria(Integer cargaHoraria) { this.cargaHoraria = cargaHoraria; return this; }
        public CertificadoResponseBuilder dataEmissao(LocalDate dataEmissao) { this.dataEmissao = dataEmissao; return this; }
        public CertificadoResponseBuilder status(String status) { this.status = status; return this; }
        public CertificadoResponseBuilder arquivoUrl(String arquivoUrl) { this.arquivoUrl = arquivoUrl; return this; }
        public CertificadoResponseBuilder alunoId(Long alunoId) { this.alunoId = alunoId; return this; }
        public CertificadoResponseBuilder alunoNome(String alunoNome) { this.alunoNome = alunoNome; return this; }
        public CertificadoResponseBuilder justificativa(String justificativa) { this.justificativa = justificativa; return this; }
        public CertificadoResponseBuilder horasValidadas(Integer horasValidadas) { this.horasValidadas = horasValidadas; return this; }
        public CertificadoResponseBuilder regraId(Long regraId) { this.regraId = regraId; return this; }
        public CertificadoResponseBuilder regraDescricao(String regraDescricao) { this.regraDescricao = regraDescricao; return this; }

        public CertificadoResponse build() {
            CertificadoResponse cr = new CertificadoResponse();
            cr.setId(id);
            cr.setNome(nome);
            cr.setCargaHoraria(cargaHoraria);
            cr.setDataEmissao(dataEmissao);
            cr.setStatus(status);
            cr.setArquivoUrl(arquivoUrl);
            cr.setAlunoId(alunoId);
            cr.setAlunoNome(alunoNome);
            cr.setJustificativa(justificativa);
            cr.setHorasValidadas(horasValidadas);
            cr.setRegraId(regraId);
            cr.setRegraDescricao(regraDescricao);
            return cr;
        }
    }
}
