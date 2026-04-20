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
        Curso curso = new Curso(null, request.getNome());
        curso = cursoRepository.save(curso);
        return new CursoResponse(curso.getId(), curso.getNome());
    }

    public List<CursoResponse> listCursos() {
        return cursoRepository.findAll().stream()
                .map(c -> new CursoResponse(c.getId(), c.getNome()))
                .collect(Collectors.toList());
    }

    public CursoResponse getCursoById(Long id) {
        Curso curso = cursoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Curso não encontrado"));
        return new CursoResponse(curso.getId(), curso.getNome());
    }
}
