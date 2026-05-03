package com.gerenciamento.certificado.config;

import com.gerenciamento.certificado.entity.*;
import com.gerenciamento.certificado.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Arrays;
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
    @org.springframework.transaction.annotation.Transactional
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

        // 2. Criar Cursos
        Curso cursoAds = getOrCreateCurso("Análise e Desenvolvimento de Sistemas", 2000, "ADS", "Graduação");
        Curso cursoJogos = getOrCreateCurso("Jogos Digitais", 1800, "JOG", "Graduação");

        // 3. Criar Coordenador (Joelson)
        User coordenadorJoelson = getOrCreateUser("Joelson Jose", "joelsonjose222@gmail.com", "joelson123", Role.COORDENADOR, new HashSet<>(Arrays.asList(cursoAds, cursoJogos)), null);
        
        // Garantir vínculo bidirecional Curso -> Coordenador
        cursoAds.setCoordenador(coordenadorJoelson);
        cursoJogos.setCoordenador(coordenadorJoelson);
        cursoRepository.saveAll(Arrays.asList(cursoAds, cursoJogos));

        // 4. Criar Regras (18 no total: 3 Ensino, 3 Pesquisa, 3 Extensão para cada curso)
        List<Curso> cursos = Arrays.asList(cursoAds, cursoJogos);
        String[] tipos = {"Ensino", "Pesquisa", "Extensão"};
        
        for (Curso curso : cursos) {
            for (String tipo : tipos) {
                for (int i = 1; i <= 3; i++) {
                    String descricao = "Regra " + tipo + " " + i + " - " + curso.getSigla();
                    getOrCreateRegra(curso, tipo, descricao, 20, "Grupo " + i, "Atividade " + i, 100);
                }
            }
        }

        // Regras específicas para os certificados de teste que serão criados abaixo
        Regra regraAdsParaCert = regraRepository.findByCursoId(cursoAds.getId()).stream()
                .filter(r -> r.getTipo().equals("Extensão")).findFirst().get();
        Regra regraJogosParaCert = regraRepository.findByCursoId(cursoJogos.getId()).stream()
                .filter(r -> r.getTipo().equals("Extensão")).findFirst().get();

        // 5. Criar Turmas
        Turma turmaAds = getOrCreateTurma("ADS-2024-1", cursoAds);
        Turma turmaJogos = getOrCreateTurma("JOG-2024-1", cursoJogos);

        // 6. Gerar um PDF de teste um pouco mais "realista"
        byte[] fakePdf = ("%PDF-1.4\n" +
                "1 0 obj <</Type/Catalog/Pages 2 0 R>> endobj\n" +
                "2 0 obj <</Type/Pages/Kids[3 0 R]/Count 1>> endobj\n" +
                "3 0 obj <</Type/Page/Parent 2 0 R/MediaBox[0 0 612 792]/Contents 4 0 R>> endobj\n" +
                "4 0 obj <</Length 150>> stream\n" +
                "BT /F1 24 Tf 100 700 Td (CERTIFICADO DE CONCLUSAO) Tj\n" +
                "/F1 14 Tf 0 -50 Td (Certificamos que o aluno participou com exito) Tj\n" +
                "0 -20 Td (das atividades academicas complementares.) Tj\n" +
                "0 -40 Td (Data: 02/05/2026) Tj ET\n" +
                "endstream endobj\n" +
                "xref\n" +
                "0 5\n" +
                "0000000000 65535 f\n" +
                "0000000009 00000 n\n" +
                "0000000056 00000 n\n" +
                "0000000111 00000 n\n" +
                "0000000212 00000 n\n" +
                "trailer <</Size 5/Root 1 0 R>>\n" +
                "startxref\n" +
                "412\n" +
                "%%EOF").getBytes();

        // 7. Criar Alunos com nomes reais
        String[] nomesAds = {"Maria Oliveira", "Carlos Souza", "Ana Costa", "Pedro Silva", "Julia Lima"};
        String[] nomesJogos = {"Lucas Ferreira", "Beatriz Rocha", "Roberto Almeida", "Carla Mendes", "Gabriel Santos"};

        for (int i = 0; i < nomesAds.length; i++) {
            User aluno = getOrCreateUser(nomesAds[i], "aluno.ads" + i + "@teste.com", "aluno123", Role.ALUNO, new HashSet<>(Arrays.asList(cursoAds)), turmaAds);
            criarCertificadoSeNaoExistir(aluno, regraAdsParaCert, fakePdf, "Certificado ADS - " + nomesAds[i]);
        }

        for (int i = 0; i < nomesJogos.length; i++) {
            User aluno = getOrCreateUser(nomesJogos[i], "aluno.jogos" + i + "@teste.com", "aluno123", Role.ALUNO, new HashSet<>(Arrays.asList(cursoJogos)), turmaJogos);
            criarCertificadoSeNaoExistir(aluno, regraJogosParaCert, fakePdf, "Certificado Jogos - " + nomesJogos[i]);
        }

        System.out.println("Seeder finalizado com sucesso!");
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

    private Curso getOrCreateCurso(String nome, Integer horas, String sigla, String categoria) {
        return cursoRepository.findAll().stream()
                .filter(c -> c.getNome().equals(nome))
                .findFirst()
                .orElseGet(() -> cursoRepository.save(new Curso(null, nome, horas, sigla, categoria)));
    }

    private Regra getOrCreateRegra(Curso curso, String tipo, String descricao, Integer horas, String grupo, String requisito, Integer aproveitamento) {
        return regraRepository.findByCursoId(curso.getId()).stream()
                .filter(r -> r.getDescricao().equals(descricao))
                .findFirst()
                .orElseGet(() -> {
                    Regra r = new Regra();
                    r.setCurso(curso);
                    r.setTipo(tipo);
                    r.setGrupo(grupo);
                    r.setDescricao(descricao);
                    r.setAproveitamento(aproveitamento + "%");
                    r.setRequisito(requisito);
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
