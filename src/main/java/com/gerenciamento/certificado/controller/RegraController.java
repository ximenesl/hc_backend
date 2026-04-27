package com.gerenciamento.certificado.controller;

import com.gerenciamento.certificado.dto.RegraRequest;
import com.gerenciamento.certificado.dto.RegraResponse;
import com.gerenciamento.certificado.service.RegraService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/regras")
public class RegraController {

    private final RegraService regraService;

    public RegraController(RegraService regraService) {
        this.regraService = regraService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('COORDENADOR', 'ADMIN')")
    public ResponseEntity<RegraResponse> createRegra(@Valid @RequestBody RegraRequest request) {
        return ResponseEntity.ok(regraService.createRegra(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('COORDENADOR', 'ADMIN')")
    public ResponseEntity<RegraResponse> updateRegra(@PathVariable Long id, @Valid @RequestBody RegraRequest request) {
        return ResponseEntity.ok(regraService.updateRegra(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('COORDENADOR', 'ADMIN')")
    public ResponseEntity<Void> deleteRegra(@PathVariable Long id) {
        regraService.deleteRegra(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<RegraResponse>> listAllRegras() {
        return ResponseEntity.ok(regraService.listAllRegras());
    }

    @GetMapping("/curso/{cursoId}")
    public ResponseEntity<List<RegraResponse>> listRegrasPorCurso(@PathVariable Long cursoId) {
        return ResponseEntity.ok(regraService.listRegrasPorCurso(cursoId));
    }
}
