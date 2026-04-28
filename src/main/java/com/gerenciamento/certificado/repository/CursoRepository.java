package com.gerenciamento.certificado.repository;

import com.gerenciamento.certificado.entity.Curso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CursoRepository extends JpaRepository<Curso, Long> {
    boolean existsByNome(String nome);
    Optional<Curso> findByNome(String nome);
    java.util.List<Curso> findByCoordenador(com.gerenciamento.certificado.entity.User coordenador);
}
