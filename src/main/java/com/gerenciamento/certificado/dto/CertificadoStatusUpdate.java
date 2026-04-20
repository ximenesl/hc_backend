package com.gerenciamento.certificado.dto;

import com.gerenciamento.certificado.entity.StatusCertificado;
import jakarta.validation.constraints.NotNull;

public class CertificadoStatusUpdate {
    @NotNull(message = "O status é obrigatório")
    private StatusCertificado status;
    private String justificativa;
    private Integer horasValidadas;

    public CertificadoStatusUpdate() {}

    public StatusCertificado getStatus() { return status; }
    public void setStatus(StatusCertificado status) { this.status = status; }
    
    public String getJustificativa() { return justificativa; }
    public void setJustificativa(String justificativa) { this.justificativa = justificativa; }
    
    public Integer getHorasValidadas() { return horasValidadas; }
    public void setHorasValidadas(Integer horasValidadas) { this.horasValidadas = horasValidadas; }
}
