package com.gerenciamento.certificado.repository;

import com.gerenciamento.certificado.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    long countByCursosIdAndRole(Long cursoId, com.gerenciamento.certificado.entity.Role role);
}
