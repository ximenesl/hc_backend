package com.gerenciamento.certificado.controller;

import com.gerenciamento.certificado.dto.UserRequest;
import com.gerenciamento.certificado.dto.UserResponse;
import com.gerenciamento.certificado.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import com.gerenciamento.certificado.repository.UserRepository;
import com.gerenciamento.certificado.entity.User;
import com.gerenciamento.certificado.exception.ResourceNotFoundException;

@RestController
@RequestMapping("/api/users")
@Tag(name = "Usuários", description = "Endpoints para gerenciamento de usuários (Requer ADMIN ou COORDENADOR)")
public class UserController {

    private final UserService userService;
    private final UserRepository userRepository;

    public UserController(UserService userService, UserRepository userRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'COORDENADOR')")
    @Operation(summary = "Criar novo usuário", description = "Apenas ADMIN ou COORDENADOR podem criar")
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody UserRequest request, Authentication authentication) {
        return new ResponseEntity<>(userService.createUser(request, authentication), HttpStatus.CREATED);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'COORDENADOR')")
    @Operation(summary = "Listar todos os usuários")
    public ResponseEntity<List<UserResponse>> listUsers(Authentication authentication) {
        return ResponseEntity.ok(userService.listUsers(authentication));
    }

    @GetMapping("/me")
    @PreAuthorize("hasAnyRole('ADMIN', 'COORDENADOR', 'ALUNO')")
    @Operation(summary = "Buscar usuário logado")
    public ResponseEntity<UserResponse> getCurrentUser(Authentication authentication) {
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));
        return ResponseEntity.ok(userService.getUserById(user.getId()));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'COORDENADOR')")
    @Operation(summary = "Buscar usuário por ID")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'COORDENADOR')")
    @Operation(summary = "Atualizar usuário", description = "Apenas ADMIN ou COORDENADOR (para alunos) pode atualizar")
    public ResponseEntity<UserResponse> updateUser(@PathVariable Long id, @Valid @RequestBody UserRequest request, Authentication authentication) {
        return ResponseEntity.ok(userService.updateUser(id, request, authentication));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'COORDENADOR')")
    @Operation(summary = "Deletar usuário", description = "Apenas ADMIN ou COORDENADOR (para alunos) pode deletar")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id, Authentication authentication) {
        userService.deleteUser(id, authentication);
        return ResponseEntity.noContent().build();
    }
}
