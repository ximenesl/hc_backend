package com.gerenciamento.certificado.service;

import com.gerenciamento.certificado.dto.CursoResponse;
import com.gerenciamento.certificado.dto.UserRequest;
import com.gerenciamento.certificado.dto.UserResponse;
import com.gerenciamento.certificado.entity.Curso;
import com.gerenciamento.certificado.entity.Role;
import com.gerenciamento.certificado.entity.User;
import com.gerenciamento.certificado.repository.CursoRepository;
import com.gerenciamento.certificado.repository.UserRepository;
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
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, CursoRepository cursoRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.cursoRepository = cursoRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserResponse createUser(UserRequest request, Authentication authentication) {
        if (authentication != null && authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_COORDENADOR"))
                && authentication.getAuthorities().stream().noneMatch(a -> a.getAuthority().equals("ROLE_SUPER_ADMIN"))) {
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

        User user = User.builder()
                .nome(request.getNome())
                .email(request.getEmail())
                .senha(passwordEncoder.encode(request.getSenha()))
                .role(request.getRole())
                .curso(cursoValido)
                .build();

        user = userRepository.save(user);
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
            cursoDto = new CursoResponse(user.getCurso().getId(), user.getCurso().getNome());
        }
        return UserResponse.builder()
                .id(user.getId())
                .nome(user.getNome())
                .email(user.getEmail())
                .role(user.getRole())
                .curso(cursoDto)
                .build();
    }
}
