package com.gerenciamento.certificado.repository;

import com.gerenciamento.certificado.entity.Regra;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RegraRepository extends JpaRepository<Regra, Long> {
    List<Regra> findByCursoId(Long cursoId);

    @Query("SELECT DISTINCT r.tipo FROM Regra r WHERE r.curso.id = :cursoId")
    List<String> findDistinctTipoByCursoId(@Param("cursoId") Long cursoId);
}

