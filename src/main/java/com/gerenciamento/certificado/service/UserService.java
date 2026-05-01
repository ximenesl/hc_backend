package com.gerenciamento.certificado.service;

import com.gerenciamento.certificado.dto.CursoResponse;
import com.gerenciamento.certificado.dto.UserRequest;
import com.gerenciamento.certificado.dto.UserResponse;
import com.gerenciamento.certificado.entity.Curso;
import com.gerenciamento.certificado.entity.Role;
import com.gerenciamento.certificado.entity.Turma;
import com.gerenciamento.certificado.entity.User;
import com.gerenciamento.certificado.repository.CursoRepository;
import com.gerenciamento.certificado.repository.TurmaRepository;
import com.gerenciamento.certificado.repository.UserRepository;
import com.gerenciamento.certificado.repository.CertificadoRepository;

import com.gerenciamento.certificado.dto.TurmaResponse;
import com.gerenciamento.certificado.exception.ResourceNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final CursoRepository cursoRepository;
    private final TurmaRepository turmaRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final CertificadoRepository certificadoRepository;

    @org.springframework.beans.factory.annotation.Value("${app.frontend-url}")
    private String frontendUrl;

    public UserService(UserRepository userRepository, CursoRepository cursoRepository, TurmaRepository turmaRepository, PasswordEncoder passwordEncoder, EmailService emailService, CertificadoRepository certificadoRepository) {
        this.userRepository = userRepository;
        this.cursoRepository = cursoRepository;
        this.turmaRepository = turmaRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
        this.certificadoRepository = certificadoRepository;
    }

    private String generateRandomPassword() {
        java.security.SecureRandom random = new java.security.SecureRandom();
        int num = random.nextInt(100000000);
        return String.format("%08d", num);
    }

    public UserResponse createUser(UserRequest request, Authentication authentication) {
        if (authentication != null && authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_COORDENADOR"))
                && authentication.getAuthorities().stream().noneMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            if (request.getRole() != Role.ALUNO) {
                throw new org.springframework.security.access.AccessDeniedException("Coordenadores só podem criar usuários do tipo ALUNO.");
            }
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email já está em uso.");
        }

        Curso cursoValido = null;
        if (request.getCursoId() != null) {
            cursoValido = cursoRepository.findById(request.getCursoId())
                .orElseThrow(() -> new ResourceNotFoundException("Curso informado não encontrado."));
        }

        Turma turmaValida = null;
        if (request.getTurmaId() != null) {
            turmaValida = turmaRepository.findById(request.getTurmaId())
                .orElseThrow(() -> new ResourceNotFoundException("Turma informada não encontrada."));
        }

        String rawPassword = request.getSenha();
        if (rawPassword == null || rawPassword.trim().isEmpty()) {
            rawPassword = generateRandomPassword();
        }

        User user = User.builder()
                .nome(request.getNome())
                .email(request.getEmail())
                .senha(passwordEncoder.encode(rawPassword))
                .role(request.getRole())
                .turma(turmaValida)
                .build();

        if (request.getCursoId() != null) {
            user.getCursos().add(cursoValido);
        } else if (request.getCursoIds() != null && !request.getCursoIds().isEmpty()) {
            java.util.List<Curso> cursos = cursoRepository.findAllById(request.getCursoIds());
            user.getCursos().addAll(cursos);
        }

        user = userRepository.save(user);

        if (user.getRole() == Role.COORDENADOR && cursoValido != null) {
            cursoValido.setCoordenador(user);
            cursoRepository.save(cursoValido);
        }
        
        try {
            String html = "<p>Olá " + user.getNome() + ",</p>"
                    + "<p>Sua conta no sistema de certificados foi criada.</p>"
                    + "<p>Sua senha provisória de acesso é: <strong>" + rawPassword + "</strong></p>"
                    + "<p>Recomendamos que você altere sua senha clicando <a href=\"" + frontendUrl + "/redefine-password\">aqui</a>.</p>";
            emailService.enviarEmail(user.getEmail(), "Sua conta foi criada - Sistema de Certificados", html);
        } catch (Exception e) {

            System.err.println("Erro ao enviar email: " + e.getMessage());
            // We don't rethrow to allow the user creation to succeed even if email fails (Resend limit/test mode)
        }

        return mapToResponse(user);
    }

    public List<UserResponse> listUsers() {
        return userRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));
        return mapToResponse(user);
    }

    public UserResponse updateUser(Long id, UserRequest request, Authentication authentication) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        if (authentication != null && authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_COORDENADOR"))
                && authentication.getAuthorities().stream().noneMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            if (user.getRole() != Role.ALUNO) {
                throw new org.springframework.security.access.AccessDeniedException("Coordenadores só podem editar usuários do tipo ALUNO.");
            }
        }

        if (!user.getEmail().equals(request.getEmail()) && userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email já está em uso.");
        }

        user.setNome(request.getNome());
        user.setEmail(request.getEmail());
        if (request.getSenha() != null && !request.getSenha().trim().isEmpty()) {
            user.setSenha(passwordEncoder.encode(request.getSenha()));
        }
        
        user.setRole(request.getRole());

        if (request.getCursoId() != null) {
            Curso cursoValido = cursoRepository.findById(request.getCursoId())
                .orElseThrow(() -> new ResourceNotFoundException("Curso informado não encontrado."));
            
            user.getCursos().clear();
            user.getCursos().add(cursoValido);

            if (user.getRole() == Role.COORDENADOR) {
                cursoValido.setCoordenador(user);
                cursoRepository.save(cursoValido);
            }
        } else if (request.getCursoIds() != null) {
            user.getCursos().clear();
            if (!request.getCursoIds().isEmpty()) {
                java.util.List<Curso> cursos = cursoRepository.findAllById(request.getCursoIds());
                user.getCursos().addAll(cursos);
                
                if (user.getRole() == Role.COORDENADOR) {
                    for (Curso c : cursos) {
                        c.setCoordenador(user);
                        cursoRepository.save(c);
                    }
                }
            }
        } else {
            user.getCursos().clear();
        }

        if (request.getTurmaId() != null) {
            Turma turmaValida = turmaRepository.findById(request.getTurmaId())
                .orElseThrow(() -> new ResourceNotFoundException("Turma informada não encontrada."));
            user.setTurma(turmaValida);
        }

        user = userRepository.save(user);
        return mapToResponse(user);
    }

    public void deleteUser(Long id, Authentication authentication) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        if (authentication != null && authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_COORDENADOR"))
                && authentication.getAuthorities().stream().noneMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            if (user.getRole() != Role.ALUNO) {
                throw new org.springframework.security.access.AccessDeniedException("Coordenadores só podem excluir usuários do tipo ALUNO.");
            }
        }
        // If the user is a coordinator, remove the coordinator reference from its courses
        if (user.getRole() == Role.COORDENADOR) {
            java.util.List<com.gerenciamento.certificado.entity.Curso> cursos = cursoRepository.findByCoordenador(user);
            for (com.gerenciamento.certificado.entity.Curso c : cursos) {
                c.setCoordenador(null);
                cursoRepository.save(c);
            }
        }
        userRepository.delete(user);
    }

    private UserResponse mapToResponse(User user) {
        java.util.Set<Curso> todosCursos = new java.util.HashSet<>(user.getCursos());
        
        // Se for coordenador, buscar cursos onde ele está definido como coordenador na tabela de cursos
        if (user.getRole() == Role.COORDENADOR) {
            todosCursos.addAll(cursoRepository.findByCoordenador(user));
        }

        java.util.List<CursoResponse> cursosDto = todosCursos.stream()
                .map(c -> new CursoResponse(c.getId(), c.getNome(), c.getHorasTotais()))
                .collect(Collectors.toList());
        
        TurmaResponse turmaDto = null;
        if (user.getTurma() != null) {
            turmaDto = new TurmaResponse(user.getTurma());
        }
        Integer horas = 0;
        if (user.getRole() == Role.ALUNO) {
            horas = certificadoRepository.sumHorasAprovadasByAlunoId(user.getId());
        }

        return UserResponse.builder()
                .id(user.getId())
                .nome(user.getNome())
                .email(user.getEmail())
                .role(user.getRole())
                .cursos(cursosDto)
                .turma(turmaDto)
                .horasAprovadas(horas)
                .build();
    }
}
