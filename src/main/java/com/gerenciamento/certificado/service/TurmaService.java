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
    private final com.gerenciamento.certificado.repository.CertificadoRepository certificadoRepository;

    public TurmaService(TurmaRepository turmaRepository, CursoRepository cursoRepository, com.gerenciamento.certificado.repository.UserRepository userRepository, com.gerenciamento.certificado.repository.CertificadoRepository certificadoRepository) {
        this.turmaRepository = turmaRepository;
        this.cursoRepository = cursoRepository;
        this.userRepository = userRepository;
        this.certificadoRepository = certificadoRepository;
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

    @org.springframework.transaction.annotation.Transactional
    public void deleteTurma(Long id) {
        Turma turma = turmaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Turma não encontrada com o ID: " + id));
        
        List<com.gerenciamento.certificado.entity.User> alunos = userRepository.findByTurmaId(id);
        for(com.gerenciamento.certificado.entity.User aluno : alunos) {
            certificadoRepository.deleteByAlunoId(aluno.getId());
            userRepository.delete(aluno);
        }
        
        turmaRepository.delete(turma);
        turmaRepository.flush();
    }

    public void inactivateTurma(Long id) {
        Turma turma = turmaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Turma não encontrada com o ID: " + id));
        
        turma.setAtivo(false);
        turmaRepository.save(turma);
    }

}
