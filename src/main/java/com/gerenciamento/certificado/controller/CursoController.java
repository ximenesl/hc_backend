package com.gerenciamento.certificado.controller;

import com.gerenciamento.certificado.dto.CursoRequest;
import com.gerenciamento.certificado.dto.CursoResponse;
import com.gerenciamento.certificado.service.CursoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cursos")
@Tag(name = "Cursos", description = "Endpoints para gerenciamento de cursos (Requer SUPER_ADMIN para criar)")
public class CursoController {

    private final CursoService cursoService;

    public CursoController(CursoService cursoService) {
        this.cursoService = cursoService;
    }

    @PostMapping
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Operation(summary = "Criar novo curso", description = "Apenas SUPER_ADMIN pode criar")
    public ResponseEntity<CursoResponse> createCurso(@Valid @RequestBody CursoRequest request) {
        return new ResponseEntity<>(cursoService.createCurso(request), HttpStatus.CREATED);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'COORDENADOR', 'ALUNO')")
    @Operation(summary = "Listar cursos")
    public ResponseEntity<List<CursoResponse>> listCursos() {
        return ResponseEntity.ok(cursoService.listCursos());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'COORDENADOR', 'ALUNO')")
    @Operation(summary = "Buscar curso por ID")
    public ResponseEntity<CursoResponse> getCursoById(@PathVariable Long id) {
        return ResponseEntity.ok(cursoService.getCursoById(id));
    }
}
