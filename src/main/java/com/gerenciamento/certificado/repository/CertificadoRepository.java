package com.gerenciamento.certificado.repository;

import com.gerenciamento.certificado.entity.Certificado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CertificadoRepository extends JpaRepository<Certificado, Long> {
    
    List<Certificado> findByAlunoId(Long alunoId);

    @org.springframework.data.jpa.repository.Modifying
    @Query("DELETE FROM Certificado c WHERE c.aluno.id = :alunoId")
    void deleteByAlunoId(@Param("alunoId") Long alunoId);

    @org.springframework.data.jpa.repository.Modifying
    @Query("DELETE FROM Certificado c WHERE c.regra.id = :regraId")
    void deleteByRegraId(@Param("regraId") Long regraId);

    long countByRegraId(Long regraId);

    long countByRegraIdAndStatus(Long regraId, com.gerenciamento.certificado.entity.StatusCertificado status);

    @Query("SELECT c FROM Certificado c JOIN c.aluno a JOIN a.cursos cur WHERE cur.id IN :cursoIds")
    List<Certificado> findByAlunoCursosIds(@Param("cursoIds") java.util.Set<Long> cursoIds);

    @Query("SELECT COALESCE(SUM(c.cargaHoraria), 0) FROM Certificado c WHERE c.aluno.id = :alunoId AND c.status = 'APROVADO'")
    Integer sumHorasAprovadasByAlunoId(@Param("alunoId") Long alunoId);
}
