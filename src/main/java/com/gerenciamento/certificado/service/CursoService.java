package com.gerenciamento.certificado.service;

import com.gerenciamento.certificado.dto.CursoRequest;
import com.gerenciamento.certificado.dto.CursoResponse;
import com.gerenciamento.certificado.entity.Curso;
import com.gerenciamento.certificado.entity.Role;
import com.gerenciamento.certificado.entity.User;
import com.gerenciamento.certificado.exception.ResourceNotFoundException;
import com.gerenciamento.certificado.repository.CursoRepository;
import com.gerenciamento.certificado.repository.TurmaRepository;
import com.gerenciamento.certificado.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CursoService {

    private final CursoRepository cursoRepository;
    private final UserRepository userRepository;
    private final TurmaRepository turmaRepository;

    public CursoService(CursoRepository cursoRepository, UserRepository userRepository, TurmaRepository turmaRepository) {
        this.cursoRepository = cursoRepository;
        this.userRepository = userRepository;
        this.turmaRepository = turmaRepository;
    }

    public CursoResponse createCurso(CursoRequest request) {
        if (cursoRepository.existsByNome(request.getNome())) {
            throw new IllegalArgumentException("O curso já está cadastrado.");
        }
        Curso curso = new Curso(null, request.getNome(), request.getHorasTotais() != null ? request.getHorasTotais() : 100);
        curso.setSigla(request.getSigla());
        curso.setCategoria(request.getCategoria());

        if (request.getCoordenadorId() != null) {
            User coordenador = userRepository.findById(request.getCoordenadorId())
                    .orElseThrow(() -> new ResourceNotFoundException("Coordenador não encontrado"));
            if (coordenador.getRole() != Role.COORDENADOR) {
                throw new IllegalArgumentException("O usuário selecionado não é um coordenador.");
            }
            curso.setCoordenador(coordenador);
        }

        curso = cursoRepository.save(curso);
        
        // Sincronizar relacionamento bidirecional
        if (curso.getCoordenador() != null) {
            User coord = curso.getCoordenador();
            coord.getCursos().add(curso);
            userRepository.save(coord);
        }

        return mapToResponse(curso);
    }

    public List<CursoResponse> listCursos(Authentication authentication) {
        if (authentication != null && authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_COORDENADOR"))
                && authentication.getAuthorities().stream().noneMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            User user = userRepository.findByEmail(authentication.getName())
                    .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));
            return cursoRepository.findByCoordenador(user).stream()
                    .map(this::mapToResponse)
                    .collect(Collectors.toList());
        }
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
        curso.setSigla(request.getSigla());
        curso.setCategoria(request.getCategoria());

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

        // Sincronizar relacionamento bidirecional
        if (curso.getCoordenador() != null) {
            User coord = curso.getCoordenador();
            if (!coord.getCursos().contains(curso)) {
                coord.getCursos().add(curso);
                userRepository.save(coord);
            }
        }
        
        return mapToResponse(curso);
    }

    public void deleteCurso(Long id) {
        Curso curso = cursoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Curso não encontrado"));

        // Check for students
        if (userRepository.countByCursosIdAndRole(id, Role.ALUNO) > 0) {
            throw new IllegalArgumentException("Não é possível excluir o curso pois existem alunos vinculados a ele.");
        }

        // Check for turmas
        if (!turmaRepository.findByCursoId(id).isEmpty()) {
            throw new IllegalArgumentException("Não é possível excluir o curso pois existem turmas vinculadas a ele.");
        }

        // Clear associations in user_cursos (coordinators, etc)
        List<User> users = userRepository.findByCursosId(id);
        for (User user : users) {
            user.getCursos().remove(curso);
            userRepository.save(user);
        }

        cursoRepository.delete(curso);
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
        
        long studentsCount = userRepository.countByCursosIdAndRole(curso.getId(), Role.ALUNO);

        return new CursoResponse(
                curso.getId(),
                curso.getNome(),
                curso.getHorasTotais(),
                coordId,
                coordNome,
                coordEmail,
                curso.getDataCriacao(),
                studentsCount,
                curso.getSigla(),
                curso.getCategoria()
        );
    }
}

