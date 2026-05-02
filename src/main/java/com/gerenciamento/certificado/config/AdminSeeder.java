package com.gerenciamento.certificado.config;

import com.gerenciamento.certificado.entity.*;
import com.gerenciamento.certificado.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class AdminSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final CursoRepository cursoRepository;
    private final TurmaRepository turmaRepository;
    private final RegraRepository regraRepository;
    private final CertificadoRepository certificadoRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminSeeder(UserRepository userRepository, 
                       CursoRepository cursoRepository, 
                       TurmaRepository turmaRepository, 
                       RegraRepository regraRepository, 
                       CertificadoRepository certificadoRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.cursoRepository = cursoRepository;
        this.turmaRepository = turmaRepository;
        this.regraRepository = regraRepository;
        this.certificadoRepository = certificadoRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        // 1. ADMIN
        if (!userRepository.existsByEmail("admin@admin.com")) {
            User admin = User.builder()
                    .nome("Administrador do Sistema")
                    .email("admin@admin.com")
                    .senha(passwordEncoder.encode("admin123"))
                    .role(Role.ADMIN)
                    .build();
            userRepository.save(admin);
        }

        // 2. CURSOS
        Curso ads = getOrCreateCurso("Análise e Desenvolvimento de Sistemas (ADS)", 100);
        
        // 3. REGRAS (Categorias)
        Regra regraAds = getOrCreateRegra(ads, "Palestras e Eventos");
        
        // 4. TURMAS
        Turma turmaAds = getOrCreateTurma("ADS - 2024.1", ads);

        // 5. PDF de teste (Mínimo válido)
        byte[] fakePdf = ("%PDF-1.4\n" +
                "1 0 obj <</Type/Catalog/Pages 2 0 R>> endobj\n" +
                "2 0 obj <</Type/Pages/Kids[3 0 R]/Count 1>> endobj\n" +
                "3 0 obj <</Type/Page/Parent 2 0 R/MediaBox[0 0 612 792]/Contents 4 0 R>> endobj\n" +
                "4 0 obj <</Length 44>> stream\n" +
                "BT /F1 24 Tf 100 700 Td (Certificado de Teste) Tj ET\n" +
                "endstream endobj\n" +
                "xref\n" +
                "0 5\n" +
                "0000000000 65535 f\n" +
                "0000000009 00000 n\n" +
                "0000000058 00000 n\n" +
                "0000000115 00000 n\n" +
                "0000000212 00000 n\n" +
                "trailer <</Size 5/Root 1 0 R>>\n" +
                "startxref\n" +
                "310\n" +
                "%%EOF").getBytes();

        // 6. Criar certificados para os alunos existentes ou novos
        List<User> alunos = userRepository.findAll().stream()
                .filter(u -> u.getRole() == Role.ALUNO)
                .toList();

        if (alunos.isEmpty()) {
            // Se não tem nenhum aluno, cria um só para o teste não ficar vazio
            User aluno = getOrCreateUser("Aluno Teste", "aluno@teste.com", "aluno123", Role.ALUNO, null, turmaAds);
            criarCertificadoSeNaoExistir(aluno, regraAds, fakePdf, "Certificado Inicial");
        } else {
            // Adiciona certificados para os alunos que já existem (limite de 5 para o teste)
            int count = 0;
            for (User aluno : alunos) {
                if (count >= 5) break;
                criarCertificadoSeNaoExistir(aluno, regraAds, fakePdf, "Certificado de " + aluno.getNome());
                count++;
            }
        }

        System.out.println("Seeder finalizado!");
    }

    private void criarCertificadoSeNaoExistir(User aluno, Regra regra, byte[] arquivo, String nome) {
        if (certificadoRepository.findByAlunoId(aluno.getId()).isEmpty()) {
            Certificado cert = Certificado.builder()
                    .nome(nome)
                    .cargaHoraria(20)
                    .dataEmissao(LocalDate.now())
                    .status(StatusCertificado.PENDENTE)
                    .arquivoDados(arquivo)
                    .arquivoTipo("application/pdf")
                    .aluno(aluno)
                    .regra(regra)
                    .build();
            certificadoRepository.save(cert);
            System.out.println("Certificado criado para: " + aluno.getNome());
        }
    }

    private Curso getOrCreateCurso(String nome, Integer horas) {
        return cursoRepository.findAll().stream()
                .filter(c -> c.getNome().equals(nome))
                .findFirst()
                .orElseGet(() -> cursoRepository.save(new Curso(null, nome, horas)));
    }

    private Regra getOrCreateRegra(Curso curso, String descricao) {
        return regraRepository.findByCursoId(curso.getId()).stream()
                .filter(r -> r.getDescricao().equals(descricao))
                .findFirst()
                .orElseGet(() -> {
                    Regra r = new Regra();
                    r.setCurso(curso);
                    r.setTipo("Extensão");
                    r.setGrupo("Grupo Geral");
                    r.setDescricao(descricao);
                    r.setAproveitamento("100%");
                    r.setRequisito("Certificado de participação");
                    return regraRepository.save(r);
                });
    }

    private Turma getOrCreateTurma(String nome, Curso curso) {
        return turmaRepository.findByCursoId(curso.getId()).stream()
                .filter(t -> t.getNome().equals(nome))
                .findFirst()
                .orElseGet(() -> turmaRepository.save(new Turma(null, nome, curso)));
    }

    private User getOrCreateUser(String nome, String email, String senha, Role role, Set<Curso> cursos, Turma turma) {
        return userRepository.findByEmail(email).orElseGet(() -> {
            User user = User.builder()
                    .nome(nome)
                    .email(email)
                    .senha(passwordEncoder.encode(senha))
                    .role(role)
                    .cursos(cursos != null ? cursos : new HashSet<>())
                    .turma(turma)
                    .build();
            return userRepository.save(user);
        });
    }
}
