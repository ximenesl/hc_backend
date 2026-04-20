package com.gerenciamento.certificado.dto;

import com.gerenciamento.certificado.entity.StatusCertificado;
import jakarta.validation.constraints.NotNull;

public class CertificadoStatusUpdate {
    @NotNull(message = "O status é obrigatório")
    private StatusCertificado status;

    public CertificadoStatusUpdate() {}

    public StatusCertificado getStatus() { return status; }
    public void setStatus(StatusCertificado status) { this.status = status; }
}
