package com.gerenciamento.certificado.service;

import com.gerenciamento.certificado.dto.CursoRequest;
import com.gerenciamento.certificado.dto.CursoResponse;
import com.gerenciamento.certificado.entity.Curso;
import com.gerenciamento.certificado.entity.Role;
import com.gerenciamento.certificado.entity.User;
import com.gerenciamento.certificado.exception.ResourceNotFoundException;
import com.gerenciamento.certificado.repository.CursoRepository;
import com.gerenciamento.certificado.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CursoService {

    private final CursoRepository cursoRepository;
    private final UserRepository userRepository;

    public CursoService(CursoRepository cursoRepository, UserRepository userRepository) {
        this.cursoRepository = cursoRepository;
        this.userRepository = userRepository;
    }

    public CursoResponse createCurso(CursoRequest request) {
        if (cursoRepository.existsByNome(request.getNome())) {
            throw new IllegalArgumentException("O curso já está cadastrado.");
        }
        Curso curso = new Curso(null, request.getNome(), request.getHorasTotais() != null ? request.getHorasTotais() : 100);

        if (request.getCoordenadorId() != null) {
            User coordenador = userRepository.findById(request.getCoordenadorId())
                    .orElseThrow(() -> new ResourceNotFoundException("Coordenador não encontrado"));
            if (coordenador.getRole() != Role.COORDENADOR) {
                throw new IllegalArgumentException("O usuário selecionado não é um coordenador.");
            }
            curso.setCoordenador(coordenador);
        }

        curso = cursoRepository.save(curso);
        return mapToResponse(curso);
    }

    public List<CursoResponse> listCursos() {
        return cursoRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public CursoResponse getCursoById(Long id) {
        Curso curso = cursoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Curso não encontrado"));
        return mapToResponse(curso);
    }

    public CursoResponse updateCurso(Long id, CursoRequest request) {
        Curso curso = cursoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Curso não encontrado"));
        
        curso.setNome(request.getNome());
        if (request.getHorasTotais() != null) {
            curso.setHorasTotais(request.getHorasTotais());
        }

        if (request.getCoordenadorId() != null) {
            User coordenador = userRepository.findById(request.getCoordenadorId())
                    .orElseThrow(() -> new ResourceNotFoundException("Coordenador não encontrado"));
            if (coordenador.getRole() != Role.COORDENADOR) {
                throw new IllegalArgumentException("O usuário selecionado não é um coordenador.");
            }
            curso.setCoordenador(coordenador);
        } else {
            curso.setCoordenador(null);
        }
        
        curso = cursoRepository.save(curso);
        return mapToResponse(curso);
    }

    public void deleteCurso(Long id) {
        if (!cursoRepository.existsById(id)) {
            throw new ResourceNotFoundException("Curso não encontrado");
        }
        cursoRepository.deleteById(id);
    }

    private CursoResponse mapToResponse(Curso curso) {
        Long coordId = null;
        String coordNome = null;
        String coordEmail = null;
        if (curso.getCoordenador() != null) {
            coordId = curso.getCoordenador().getId();
            coordNome = curso.getCoordenador().getNome();
            coordEmail = curso.getCoordenador().getEmail();
        }
        return new CursoResponse(
                curso.getId(),
                curso.getNome(),
                curso.getHorasTotais(),
                coordId,
                coordNome,
                coordEmail
        );
    }
}

