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
            System.out.println("Usuário ADMIN criado!");
        }

        // 2. CURSOS
        Curso ads = getOrCreateCurso("Análise e Desenvolvimento de Sistemas (ADS)", 100);
        Curso engCivil = getOrCreateCurso("Engenharia Civil", 120);
        Curso direito = getOrCreateCurso("Direito", 150);

        // 3. REGRAS (Categorias)
        Regra regraAds = getOrCreateRegra(ads, "Palestras e Eventos");
        Regra regraEng = getOrCreateRegra(engCivil, "Visitas Técnicas");
        Regra regraDir = getOrCreateRegra(direito, "Estágio Curricular");

        // 4. TURMAS
        Turma turmaAds = getOrCreateTurma("ADS - 2024.1", ads);
        Turma turmaEng = getOrCreateTurma("CIVIL - 2024.1", engCivil);
        Turma turmaDir = getOrCreateTurma("DIREITO - 2024.1", direito);

        // 5. COORDENADORES
        User coordJoelson = getOrCreateUser("Joelson Santos", "joelson@teste.com", "coord123", Role.COORDENADOR, Set.of(ads, engCivil), null);
        User coordMaria = getOrCreateUser("Maria Ferreira", "maria@teste.com", "coord123", Role.COORDENADOR, Set.of(direito), null);

        // 6. ALUNOS E CERTIFICADOS
        byte[] fakePdf = "Conteudo de teste do certificado PDF".getBytes();

        // Alunos para o Joelson (ADS)
        for (int i = 1; i <= 5; i++) {
            User aluno = getOrCreateUser("Aluno ADS " + i, "aluno.ads" + i + "@teste.com", "aluno123", Role.ALUNO, null, turmaAds);
            if (certificadoRepository.findByAlunoId(aluno.getId()).isEmpty()) {
                Certificado cert = Certificado.builder()
                        .nome("Certificado Palestra " + i)
                        .cargaHoraria(10 + i)
                        .dataEmissao(LocalDate.now().minusDays(i))
                        .status(StatusCertificado.PENDENTE)
                        .arquivoDados(fakePdf)
                        .arquivoTipo("application/pdf")
                        .aluno(aluno)
                        .regra(regraAds)
                        .build();
                certificadoRepository.save(cert);
            }
        }

        // Alunos para a Maria (Direito)
        for (int i = 1; i <= 5; i++) {
            User aluno = getOrCreateUser("Aluno Direito " + i, "aluno.direito" + i + "@teste.com", "aluno123", Role.ALUNO, null, turmaDir);
            if (certificadoRepository.findByAlunoId(aluno.getId()).isEmpty()) {
                Certificado cert = Certificado.builder()
                        .nome("Certificado Estágio " + i)
                        .cargaHoraria(20 + i)
                        .dataEmissao(LocalDate.now().minusDays(i))
                        .status(StatusCertificado.PENDENTE)
                        .arquivoDados(fakePdf)
                        .arquivoTipo("application/pdf")
                        .aluno(aluno)
                        .regra(regraDir)
                        .build();
                certificadoRepository.save(cert);
            }
        }
        
        System.out.println("Seeder finalizado com sucesso!");
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
