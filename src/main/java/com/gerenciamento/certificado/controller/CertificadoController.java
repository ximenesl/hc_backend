package com.gerenciamento.certificado.controller;

import com.gerenciamento.certificado.dto.CertificadoResponse;
import com.gerenciamento.certificado.dto.CertificadoStatusUpdate;
import com.gerenciamento.certificado.service.CertificadoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/certificates")
@Tag(name = "Certificados", description = "Endpoints de certificados e upload de pdf")
public class CertificadoController {

    private final CertificadoService certificadoService;

    public CertificadoController(CertificadoService certificadoService) {
        this.certificadoService = certificadoService;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ALUNO')")
    @Operation(summary = "Enviar Certificado", description = "Aluno envia um PDF. Status inicial é PENDENTE.")
    public ResponseEntity<CertificadoResponse> upload(
            @RequestParam("alunoId") Long alunoId,
            @RequestParam("nome") String nome,
            @RequestParam("cargaHoraria") Integer cargaHoraria,
            @RequestParam("dataEmissao") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataEmissao,
            @RequestParam("arquivo") MultipartFile arquivo) {
            
        return new ResponseEntity<>(
            certificadoService.uploadCertificado(alunoId, nome, cargaHoraria, dataEmissao, arquivo), 
            HttpStatus.CREATED
        );
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'COORDENADOR')")
    @Operation(summary = "Listar todos os certificados", description = "Visão geral para a coordenação")
    public ResponseEntity<List<CertificadoResponse>> listarGeral() {
        return ResponseEntity.ok(certificadoService.listarTodos());
    }

    @GetMapping("/me/{alunoId}")
    @PreAuthorize("hasAnyRole('ALUNO', 'COORDENADOR', 'SUPER_ADMIN')")
    @Operation(summary = "Listar certificados do aluno")
    public ResponseEntity<List<CertificadoResponse>> listarPorAluno(@PathVariable Long alunoId) {
        return ResponseEntity.ok(certificadoService.listarPorAluno(alunoId));
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'COORDENADOR')")
    @Operation(summary = "Aprovar ou Rejeitar Certificado", description = "Muda o status do certificado")
    public ResponseEntity<CertificadoResponse> atualizarStatus(
            @PathVariable Long id,
            @Valid @RequestBody CertificadoStatusUpdate request) {
        return ResponseEntity.ok(certificadoService.atualizarStatus(id, request));
    }

    @GetMapping("/horas/{alunoId}")
    @PreAuthorize("hasAnyRole('ALUNO', 'COORDENADOR', 'SUPER_ADMIN')")
    @Operation(summary = "Somar horas aprovadas", description = "Retorna a soma automática das horas validadas")
    public ResponseEntity<Integer> obterHorasAprovadas(@PathVariable Long alunoId) {
        return ResponseEntity.ok(certificadoService.calcularHorasAprovadas(alunoId));
    }

    @GetMapping("/{id}/file")
    @PreAuthorize("hasAnyRole('ALUNO', 'COORDENADOR', 'SUPER_ADMIN')")
    @Operation(summary = "Baixar/Visualizar arquivo do certificado", description = "Retorna o arquivo do certificado")
    public ResponseEntity<org.springframework.core.io.Resource> getCertificadoFile(@PathVariable Long id) {
        return certificadoService.getFileAsResource(id);
    }
}
