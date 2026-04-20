package com.gerenciamento.certificado.service;

import com.gerenciamento.certificado.dto.CertificadoResponse;
import com.gerenciamento.certificado.dto.CertificadoStatusUpdate;
import com.gerenciamento.certificado.entity.Certificado;
import com.gerenciamento.certificado.entity.StatusCertificado;
import com.gerenciamento.certificado.entity.User;
import com.gerenciamento.certificado.repository.CertificadoRepository;
import com.gerenciamento.certificado.repository.UserRepository;
import com.gerenciamento.certificado.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CertificadoService {

    private final CertificadoRepository certificadoRepository;
    private final UserRepository userRepository;
    private final FileStorageService fileStorageService;

    public CertificadoService(CertificadoRepository certificadoRepository, UserRepository userRepository, FileStorageService fileStorageService) {
        this.certificadoRepository = certificadoRepository;
        this.userRepository = userRepository;
        this.fileStorageService = fileStorageService;
    }

    public CertificadoResponse uploadCertificado(Long alunoId, String nome, Integer cargaHoraria, LocalDate dataEmissao, MultipartFile arquivo) {
        User aluno = userRepository.findById(alunoId)
                .orElseThrow(() -> new ResourceNotFoundException("Aluno não encontrado"));

        String arquivoPath = fileStorageService.storeFile(arquivo);

        Certificado certificado = Certificado.builder()
                .nome(nome)
                .cargaHoraria(cargaHoraria)
                .dataEmissao(dataEmissao)
                .status(StatusCertificado.PENDENTE)
                .arquivoPath(arquivoPath)
                .aluno(aluno)
                .build();

        certificado = certificadoRepository.save(certificado);
        return mapToResponse(certificado);
    }

    public List<CertificadoResponse> listarTodos() {
        return certificadoRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<CertificadoResponse> listarPorAluno(Long alunoId) {
        if (!userRepository.existsById(alunoId)) {
            throw new ResourceNotFoundException("Aluno não encontrado");
        }
        return certificadoRepository.findByAlunoId(alunoId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public CertificadoResponse atualizarStatus(Long id, CertificadoStatusUpdate request) {
        Certificado certificado = certificadoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Certificado não encontrado"));

        certificado.setStatus(request.getStatus());
        if (request.getJustificativa() != null) {
            certificado.setJustificativa(request.getJustificativa());
        }
        if (request.getHorasValidadas() != null) {
            certificado.setHorasValidadas(request.getHorasValidadas());
        }
        
        certificado = certificadoRepository.save(certificado);

        return mapToResponse(certificado);
    }

    public Integer calcularHorasAprovadas(Long alunoId) {
        if (!userRepository.existsById(alunoId)) {
            throw new ResourceNotFoundException("Aluno não encontrado");
        }
        return certificadoRepository.sumHorasAprovadasByAlunoId(alunoId);
    }

    private CertificadoResponse mapToResponse(Certificado c) {
        return CertificadoResponse.builder()
                .id(c.getId())
                .nome(c.getNome())
                .cargaHoraria(c.getCargaHoraria())
                .dataEmissao(c.getDataEmissao())
                .status(c.getStatus().name())
                .arquivoUrl("/api/certificates/" + c.getId() + "/file") 
                .alunoId(c.getAluno().getId())
                .alunoNome(c.getAluno().getNome())
                .justificativa(c.getJustificativa())
                .horasValidadas(c.getHorasValidadas())
                .build();
    }

    public org.springframework.http.ResponseEntity<org.springframework.core.io.Resource> getFileAsResource(Long id) {
        Certificado certificado = certificadoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Certificado não encontrado"));
        try {
            java.nio.file.Path path = java.nio.file.Paths.get(certificado.getArquivoPath());
            org.springframework.core.io.Resource resource = new org.springframework.core.io.UrlResource(path.toUri());
            if (resource.exists() || resource.isReadable()) {
                String contentType = "application/pdf";
                if(certificado.getArquivoPath().toLowerCase().endsWith(".png")) contentType = "image/png";
                else if(certificado.getArquivoPath().toLowerCase().endsWith(".jpg") || certificado.getArquivoPath().toLowerCase().endsWith(".jpeg")) contentType = "image/jpeg";
                return org.springframework.http.ResponseEntity.ok()
                        .contentType(org.springframework.http.MediaType.parseMediaType(contentType))
                        .header(org.springframework.http.HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                        .body(resource);
            } else {
                throw new ResourceNotFoundException("Arquivo não pode ser lido");
            }
        } catch (java.net.MalformedURLException e) {
            throw new ResourceNotFoundException("Erro ao ler o arquivo");
        }
    }
}
