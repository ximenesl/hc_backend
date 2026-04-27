package com.gerenciamento.certificado.service;

import com.gerenciamento.certificado.dto.CursoRequest;
import com.gerenciamento.certificado.dto.CursoResponse;
import com.gerenciamento.certificado.entity.Curso;
import com.gerenciamento.certificado.exception.ResourceNotFoundException;
import com.gerenciamento.certificado.repository.CursoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CursoService {

    private final CursoRepository cursoRepository;

    public CursoService(CursoRepository cursoRepository) {
        this.cursoRepository = cursoRepository;
    }

    public CursoResponse createCurso(CursoRequest request) {
        if (cursoRepository.existsByNome(request.getNome())) {
            throw new IllegalArgumentException("O curso já está cadastrado.");
        }
        Curso curso = new Curso(null, request.getNome(), request.getHorasTotais() != null ? request.getHorasTotais() : 100);
        curso = cursoRepository.save(curso);
        return new CursoResponse(curso.getId(), curso.getNome(), curso.getHorasTotais());
    }

    public List<CursoResponse> listCursos() {
        return cursoRepository.findAll().stream()
                .map(c -> new CursoResponse(c.getId(), c.getNome(), c.getHorasTotais()))
                .collect(Collectors.toList());
    }

    public CursoResponse getCursoById(Long id) {
        Curso curso = cursoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Curso não encontrado"));
        return new CursoResponse(curso.getId(), curso.getNome(), curso.getHorasTotais());
    }

    public CursoResponse updateCurso(Long id, CursoRequest request) {
        Curso curso = cursoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Curso não encontrado"));
        
        curso.setNome(request.getNome());
        if (request.getHorasTotais() != null) {
            curso.setHorasTotais(request.getHorasTotais());
        }
        
        curso = cursoRepository.save(curso);
        return new CursoResponse(curso.getId(), curso.getNome(), curso.getHorasTotais());
    }
}
