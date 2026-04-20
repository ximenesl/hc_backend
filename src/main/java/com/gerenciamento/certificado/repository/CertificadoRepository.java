package com.gerenciamento.certificado.repository;

import com.gerenciamento.certificado.entity.Certificado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CertificadoRepository extends JpaRepository<Certificado, Long> {
    
    List<Certificado> findByAlunoId(Long alunoId);

    @Query("SELECT COALESCE(SUM(c.cargaHoraria), 0) FROM Certificado c WHERE c.aluno.id = :alunoId AND c.status = 'APROVADO'")
    Integer sumHorasAprovadasByAlunoId(@Param("alunoId") Long alunoId);
}
