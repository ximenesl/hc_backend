package com.gerenciamento.certificado.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "certificados")
public class Certificado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false)
    private Integer cargaHoraria;

    @Column(nullable = false)
    private LocalDate dataEmissao;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusCertificado status;

    @Lob
    @Column(nullable = false)
    private byte[] arquivoDados;

    @Column(nullable = false)
    private String arquivoTipo;

    @Column(name = "justificativa")
    private String justificativa;

    @Column(name = "horas_validadas")
    private Integer horasValidadas;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "aluno_id", nullable = false)
    private User aluno;

    public Certificado() {}

    public Certificado(Long id, String nome, Integer cargaHoraria, LocalDate dataEmissao, StatusCertificado status, byte[] arquivoDados, String arquivoTipo, User aluno) {
        this.id = id;
        this.nome = nome;
        this.cargaHoraria = cargaHoraria;
        this.dataEmissao = dataEmissao;
        this.status = status;
        this.arquivoDados = arquivoDados;
        this.arquivoTipo = arquivoTipo;
        this.aluno = aluno;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public Integer getCargaHoraria() { return cargaHoraria; }
    public void setCargaHoraria(Integer cargaHoraria) { this.cargaHoraria = cargaHoraria; }
    public LocalDate getDataEmissao() { return dataEmissao; }
    public void setDataEmissao(LocalDate dataEmissao) { this.dataEmissao = dataEmissao; }
    public StatusCertificado getStatus() { return status; }
    public void setStatus(StatusCertificado status) { this.status = status; }
    public byte[] getArquivoDados() { return arquivoDados; }
    public void setArquivoDados(byte[] arquivoDados) { this.arquivoDados = arquivoDados; }
    public String getArquivoTipo() { return arquivoTipo; }
    public void setArquivoTipo(String arquivoTipo) { this.arquivoTipo = arquivoTipo; }
    public String getJustificativa() { return justificativa; }
    public void setJustificativa(String justificativa) { this.justificativa = justificativa; }
    public Integer getHorasValidadas() { return horasValidadas; }
    public void setHorasValidadas(Integer horasValidadas) { this.horasValidadas = horasValidadas; }
    public User getAluno() { return aluno; }
    public void setAluno(User aluno) { this.aluno = aluno; }

    public static CertificadoBuilder builder() { return new CertificadoBuilder(); }

    public static class CertificadoBuilder {
        private Long id;
        private String nome;
        private Integer cargaHoraria;
        private LocalDate dataEmissao;
        private StatusCertificado status;
        private byte[] arquivoDados;
        private String arquivoTipo;
        private User aluno;

        public CertificadoBuilder id(Long id) { this.id = id; return this; }
        public CertificadoBuilder nome(String nome) { this.nome = nome; return this; }
        public CertificadoBuilder cargaHoraria(Integer cargaHoraria) { this.cargaHoraria = cargaHoraria; return this; }
        public CertificadoBuilder dataEmissao(LocalDate dataEmissao) { this.dataEmissao = dataEmissao; return this; }
        public CertificadoBuilder status(StatusCertificado status) { this.status = status; return this; }
        public CertificadoBuilder arquivoDados(byte[] arquivoDados) { this.arquivoDados = arquivoDados; return this; }
        public CertificadoBuilder arquivoTipo(String arquivoTipo) { this.arquivoTipo = arquivoTipo; return this; }
        public CertificadoBuilder aluno(User aluno) { this.aluno = aluno; return this; }

        public Certificado build() {
            return new Certificado(id, nome, cargaHoraria, dataEmissao, status, arquivoDados, arquivoTipo, aluno);
        }
    }
}
