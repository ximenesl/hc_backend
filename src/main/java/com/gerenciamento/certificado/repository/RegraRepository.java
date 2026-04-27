package com.gerenciamento.certificado.repository;

import com.gerenciamento.certificado.entity.Regra;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RegraRepository extends JpaRepository<Regra, Long> {
    List<Regra> findByCursoId(Long cursoId);
}
