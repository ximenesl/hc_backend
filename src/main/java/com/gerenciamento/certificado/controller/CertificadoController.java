package com.gerenciamento.certificado.controller;

import com.gerenciamento.certificado.dto.CertificadoResponse;
import com.gerenciamento.certificado.dto.CertificadoStatusUpdate;
import com.gerenciamento.certificado.service.CertificadoService;
import com.gerenciamento.certificado.service.OcrService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/certificates")
@Tag(name = "Certificados", description = "Endpoints de certificados e upload de pdf")
public class CertificadoController {

    private final CertificadoService certificadoService;
    private final OcrService ocrService;

    public CertificadoController(CertificadoService certificadoService, OcrService ocrService) {
        this.certificadoService = certificadoService;
        this.ocrService = ocrService;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ALUNO')")
    @Operation(summary = "Enviar Certificado", description = "Aluno envia um PDF. Status inicial é PENDENTE.")
    public ResponseEntity<CertificadoResponse> upload(
            @RequestParam("alunoId") Long alunoId,
            @RequestParam("nome") String nome,
            @RequestParam("cargaHoraria") Integer cargaHoraria,
            @RequestParam("dataEmissao") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataEmissao,
            @RequestParam("arquivo") MultipartFile arquivo,
            @RequestParam("regraId") Long regraId) {
            
        return new ResponseEntity<>(
            certificadoService.uploadCertificado(alunoId, nome, cargaHoraria, dataEmissao, arquivo, regraId), 
            HttpStatus.CREATED
        );
    }

    @PostMapping(value = "/ocr", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ALUNO')")
    @Operation(summary = "Realizar OCR no comprovante", description = "Extrai texto e tenta identificar horas e título")
    public ResponseEntity<OcrService.OcrResult> analyzeFile(@RequestParam("arquivo") MultipartFile file) {
        try {
            byte[] bytes = file.getBytes();
            String contentType = file.getContentType();
            return ResponseEntity.ok(ocrService.analyzeFile(bytes, contentType));
        } catch (java.io.IOException e) {
            throw new RuntimeException("Erro ao processar arquivo de OCR", e);
        }
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'COORDENADOR')")
    @Operation(summary = "Listar todos os certificados", description = "Visão geral para a coordenação")
    public ResponseEntity<Page<CertificadoResponse>> listarGeral(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            java.security.Principal principal) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        return ResponseEntity.ok(certificadoService.listarTodos(principal.getName(), pageable));
    }

    @GetMapping("/me/{alunoId}")
    @PreAuthorize("hasAnyRole('ALUNO', 'COORDENADOR', 'ADMIN')")
    @Operation(summary = "Listar certificados do aluno")
    public ResponseEntity<Page<CertificadoResponse>> listarPorAluno(
            @PathVariable Long alunoId,
            @RequestParam(required = false) Long cursoId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        return ResponseEntity.ok(certificadoService.listarPorAluno(alunoId, cursoId, status, search, pageable));
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'COORDENADOR')")
    @Operation(summary = "Aprovar ou Rejeitar Certificado", description = "Muda o status do certificado")
    public ResponseEntity<CertificadoResponse> atualizarStatus(
            @PathVariable Long id,
            @Valid @RequestBody CertificadoStatusUpdate request) {
        return ResponseEntity.ok(certificadoService.atualizarStatus(id, request));
    }

    @GetMapping("/horas/{alunoId}")
    @PreAuthorize("hasAnyRole('ALUNO', 'COORDENADOR', 'ADMIN')")
    @Operation(summary = "Somar horas aprovadas", description = "Retorna a soma automática das horas validadas")
    public ResponseEntity<Integer> obterHorasAprovadas(@PathVariable Long alunoId) {
        return ResponseEntity.ok(certificadoService.calcularHorasAprovadas(alunoId));
    }

    @GetMapping("/{id}/file")
    @Operation(summary = "Baixar/Visualizar arquivo do certificado", description = "Retorna o arquivo do certificado")
    public ResponseEntity<org.springframework.core.io.Resource> getCertificadoFile(@PathVariable Long id) {
        return certificadoService.getFileAsResource(id);
    }

    @PostMapping("/reset-tests")
    @PreAuthorize("hasAnyRole('ADMIN', 'COORDENADOR')")
    @Operation(summary = "Resetar status dos certificados", description = "Volta todos os certificados para PENDENTE para fins de teste")
    public ResponseEntity<Void> resetTests() {
        certificadoService.resetTodosParaPendente();
        return ResponseEntity.ok().build();
    }
}
