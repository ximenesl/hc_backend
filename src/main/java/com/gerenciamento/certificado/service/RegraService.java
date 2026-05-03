package com.gerenciamento.certificado.service;

import com.gerenciamento.certificado.dto.RegraRequest;
import com.gerenciamento.certificado.dto.RegraResponse;
import com.gerenciamento.certificado.entity.Curso;
import com.gerenciamento.certificado.entity.Regra;
import com.gerenciamento.certificado.exception.ResourceNotFoundException;
import com.gerenciamento.certificado.repository.CursoRepository;
import com.gerenciamento.certificado.repository.RegraRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RegraService {

    private final RegraRepository regraRepository;
    private final CursoRepository cursoRepository;
    private final com.gerenciamento.certificado.repository.CertificadoRepository certificadoRepository;

    public RegraService(RegraRepository regraRepository, CursoRepository cursoRepository, com.gerenciamento.certificado.repository.CertificadoRepository certificadoRepository) {
        this.regraRepository = regraRepository;
        this.cursoRepository = cursoRepository;
        this.certificadoRepository = certificadoRepository;
    }

    public RegraResponse createRegra(RegraRequest request) {
        Curso curso = cursoRepository.findById(request.getCursoId())
                .orElseThrow(() -> new ResourceNotFoundException("Curso não encontrado"));

        Regra regra = new Regra();
        regra.setCurso(curso);
        regra.setTipo(request.getTipo());
        regra.setGrupo(request.getGrupo());
        regra.setDescricao(request.getDescricao());
        regra.setAproveitamento(request.getAproveitamento());
        regra.setRequisito(request.getRequisito());

        regra = regraRepository.save(regra);
        return mapToResponse(regra);
    }

    public RegraResponse updateRegra(Long id, RegraRequest request) {
        Regra regra = regraRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Regra não encontrada"));

        Curso curso = cursoRepository.findById(request.getCursoId())
                .orElseThrow(() -> new ResourceNotFoundException("Curso não encontrado"));

        regra.setCurso(curso);
        regra.setTipo(request.getTipo());
        regra.setGrupo(request.getGrupo());
        regra.setDescricao(request.getDescricao());
        regra.setAproveitamento(request.getAproveitamento());
        regra.setRequisito(request.getRequisito());

        regra = regraRepository.save(regra);
        return mapToResponse(regra);
    }

    @org.springframework.transaction.annotation.Transactional
    public void deleteRegra(Long id) {
        if (!regraRepository.existsById(id)) {
            throw new ResourceNotFoundException("Regra não encontrada");
        }
        
        long totalCertificados = certificadoRepository.countByRegraId(id);
        if (totalCertificados > 0) {
            long pendentes = certificadoRepository.countByRegraIdAndStatus(id, com.gerenciamento.certificado.entity.StatusCertificado.PENDENTE);
            if (pendentes > 0) {
                throw new IllegalArgumentException("Esta regra está vinculada a algum certificado que precisa de validação.");
            }
            throw new IllegalArgumentException("Esta regra não pode ser excluída pois possui certificados vinculados. Considere inativá-la.");
        }

        regraRepository.deleteById(id);
        regraRepository.flush();
    }

    public void inactivateRegra(Long id) {
        Regra regra = regraRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Regra não encontrada"));
        regra.setAtivo(false);
        regraRepository.save(regra);
    }

    public List<RegraResponse> listRegrasPorCurso(Long cursoId) {
        return regraRepository.findByCursoId(cursoId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<RegraResponse> listAllRegras() {
        return regraRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<String> listTiposPorCurso(Long cursoId) {
        return regraRepository.findDistinctTipoByCursoId(cursoId);
    }

    private RegraResponse mapToResponse(Regra regra) {
        return new RegraResponse(
                regra.getId(),
                regra.getCurso().getId(),
                regra.getTipo(),
                regra.getGrupo(),
                regra.getDescricao(),
                regra.getAproveitamento(),
                regra.getRequisito(),
                regra.getAtivo()
        );
    }
}
