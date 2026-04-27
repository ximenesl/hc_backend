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

    public UserService(UserRepository userRepository, CursoRepository cursoRepository, TurmaRepository turmaRepository, PasswordEncoder passwordEncoder, EmailService emailService) {
        this.userRepository = userRepository;
        this.cursoRepository = cursoRepository;
        this.turmaRepository = turmaRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
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
                .curso(cursoValido)
                .turma(turmaValida)
                .build();

        user = userRepository.save(user);
        
        String html = "<p>Olá " + user.getNome() + ",</p><p>Sua conta no sistema de certificados foi criada.</p><p>Sua senha provisória de acesso é: <strong>" + rawPassword + "</strong></p><p>Recomendamos que você altere sua senha após o primeiro acesso.</p>";
        emailService.enviarEmail(user.getEmail(), "Sua conta foi criada - Sistema de Certificados", html);

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

    public UserResponse updateUser(Long id, UserRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

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
            user.setCurso(cursoValido);
        }

        if (request.getTurmaId() != null) {
            Turma turmaValida = turmaRepository.findById(request.getTurmaId())
                .orElseThrow(() -> new ResourceNotFoundException("Turma informada não encontrada."));
            user.setTurma(turmaValida);
        }

        user = userRepository.save(user);
        return mapToResponse(user);
    }

    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("Usuário não encontrado");
        }
        userRepository.deleteById(id);
    }

    private UserResponse mapToResponse(User user) {
        CursoResponse cursoDto = null;
        if (user.getCurso() != null) {
            cursoDto = new CursoResponse(user.getCurso().getId(), user.getCurso().getNome(), user.getCurso().getHorasTotais());
        }
        TurmaResponse turmaDto = null;
        if (user.getTurma() != null) {
            turmaDto = new TurmaResponse(user.getTurma());
        }
        return UserResponse.builder()
                .id(user.getId())
                .nome(user.getNome())
                .email(user.getEmail())
                .role(user.getRole())
                .curso(cursoDto)
                .turma(turmaDto)
                .build();
    }
}
