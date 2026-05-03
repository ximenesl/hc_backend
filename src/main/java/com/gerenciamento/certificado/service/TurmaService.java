package com.gerenciamento.certificado.service;

import com.gerenciamento.certificado.dto.TurmaRequest;
import com.gerenciamento.certificado.dto.TurmaResponse;
import com.gerenciamento.certificado.entity.Curso;
import com.gerenciamento.certificado.entity.Turma;
import com.gerenciamento.certificado.exception.ResourceNotFoundException;
import com.gerenciamento.certificado.repository.CursoRepository;
import com.gerenciamento.certificado.repository.TurmaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TurmaService {

    private final TurmaRepository turmaRepository;
    private final CursoRepository cursoRepository;
    private final com.gerenciamento.certificado.repository.UserRepository userRepository;

    public TurmaService(TurmaRepository turmaRepository, CursoRepository cursoRepository, com.gerenciamento.certificado.repository.UserRepository userRepository) {
        this.turmaRepository = turmaRepository;
        this.cursoRepository = cursoRepository;
        this.userRepository = userRepository;
    }


    public TurmaResponse createTurma(TurmaRequest request) {
        if (turmaRepository.existsByNomeAndCursoId(request.getNome(), request.getCursoId())) {
            throw new IllegalArgumentException("Turma com este nome já existe no curso.");
        }

        Curso curso = cursoRepository.findById(request.getCursoId())
                .orElseThrow(() -> new ResourceNotFoundException("Curso não encontrado com o ID: " + request.getCursoId()));

        Turma turma = new Turma();
        turma.setNome(request.getNome());
        turma.setCurso(curso);

        Turma saved = turmaRepository.save(turma);
        return new TurmaResponse(saved);
    }

    public List<TurmaResponse> listTurmas() {
        return turmaRepository.findAll().stream()
                .map(TurmaResponse::new)
                .collect(Collectors.toList());
    }

    public TurmaResponse getTurmaById(Long id) {
        Turma turma = turmaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Turma não encontrada com o ID: " + id));
        return new TurmaResponse(turma);
    }

    public TurmaResponse updateTurma(Long id, TurmaRequest request) {
        Turma turma = turmaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Turma não encontrada com o ID: " + id));

        if (!turma.getNome().equals(request.getNome()) && turmaRepository.existsByNomeAndCursoId(request.getNome(), request.getCursoId())) {
            throw new IllegalArgumentException("Turma com este nome já existe no curso.");
        }

        Curso curso = cursoRepository.findById(request.getCursoId())
                .orElseThrow(() -> new ResourceNotFoundException("Curso não encontrado com o ID: " + request.getCursoId()));

        turma.setNome(request.getNome());
        turma.setCurso(curso);

        Turma updated = turmaRepository.save(turma);
        return new TurmaResponse(updated);
    }

    public void deleteTurma(Long id) {
        Turma turma = turmaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Turma não encontrada com o ID: " + id));
        
        if (userRepository.countByTurmaId(id) > 0) {
            throw new IllegalArgumentException("Não é possível excluir a turma pois existem alunos vinculados a ela.");
        }
        
        turma.setAtivo(false);
        turmaRepository.save(turma);
    }

}
