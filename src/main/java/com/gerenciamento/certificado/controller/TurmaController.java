package com.gerenciamento.certificado.controller;

import com.gerenciamento.certificado.dto.TurmaRequest;
import com.gerenciamento.certificado.dto.TurmaResponse;
import com.gerenciamento.certificado.service.TurmaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/turmas")
@Tag(name = "Turmas", description = "Endpoints para gerenciamento de turmas (Requer ADMIN ou COORDENADOR para criar/editar)")
public class TurmaController {

    private final TurmaService turmaService;

    public TurmaController(TurmaService turmaService) {
        this.turmaService = turmaService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'COORDENADOR')")
    @Operation(summary = "Criar nova turma")
    public ResponseEntity<TurmaResponse> createTurma(@Valid @RequestBody TurmaRequest request) {
        return new ResponseEntity<>(turmaService.createTurma(request), HttpStatus.CREATED);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'COORDENADOR', 'ALUNO')")
    @Operation(summary = "Listar todas as turmas")
    public ResponseEntity<List<TurmaResponse>> listTurmas() {
        return ResponseEntity.ok(turmaService.listTurmas());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'COORDENADOR', 'ALUNO')")
    @Operation(summary = "Buscar turma por ID")
    public ResponseEntity<TurmaResponse> getTurmaById(@PathVariable Long id) {
        return ResponseEntity.ok(turmaService.getTurmaById(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'COORDENADOR')")
    @Operation(summary = "Atualizar turma")
    public ResponseEntity<TurmaResponse> updateTurma(@PathVariable Long id, @Valid @RequestBody TurmaRequest request) {
        return ResponseEntity.ok(turmaService.updateTurma(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'COORDENADOR')")
    @Operation(summary = "Deletar turma")
    public ResponseEntity<Void> deleteTurma(@PathVariable Long id) {
        turmaService.deleteTurma(id);
        return ResponseEntity.noContent().build();
    }
}
