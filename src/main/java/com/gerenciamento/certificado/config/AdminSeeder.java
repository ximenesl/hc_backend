package com.gerenciamento.certificado.config;

import com.gerenciamento.certificado.entity.Curso;
import com.gerenciamento.certificado.entity.Role;
import com.gerenciamento.certificado.entity.User;
import com.gerenciamento.certificado.repository.CursoRepository;
import com.gerenciamento.certificado.repository.TurmaRepository;
import com.gerenciamento.certificado.repository.UserRepository;
import com.gerenciamento.certificado.entity.Turma;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AdminSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final CursoRepository cursoRepository;
    private final TurmaRepository turmaRepository;
    private final RegraRepository regraRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminSeeder(UserRepository userRepository, CursoRepository cursoRepository, TurmaRepository turmaRepository, RegraRepository regraRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.cursoRepository = cursoRepository;
        this.turmaRepository = turmaRepository;
        this.regraRepository = regraRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        if (!userRepository.existsByEmail("admin@admin.com")) {
            User admin = User.builder()
                    .nome("Admin")
                    .email("admin@admin.com")
                    .senha(passwordEncoder.encode("admin123"))
                    .role(Role.ADMIN)
                    .build();
            userRepository.save(admin);
            System.out.println("Usuário ADMIN criado com sucesso! (admin@admin.com / admin123)");
        }

        if (!userRepository.existsByEmail("coordenador@teste.com")) {
            User coordenador = User.builder()
                    .nome("Coordenador Teste")
                    .email("coordenador@teste.com")
                    .senha(passwordEncoder.encode("coord123"))
                    .role(Role.COORDENADOR)
                    .build();
            userRepository.save(coordenador);
            System.out.println("Usuário COORDENADOR criado com sucesso! (coordenador@teste.com / coord123)");
        }

        Curso cursoAds = null;
        if (!cursoRepository.existsByNome("Análise e Desenvolvimento de Sistemas (ADS)")) {
            cursoAds = cursoRepository.save(new Curso(null, "Análise e Desenvolvimento de Sistemas (ADS)", 100));
            System.out.println("Curso ADS criado com sucesso!");
        } else {
            cursoAds = cursoRepository.findAll().stream().filter(c -> c.getNome().equals("Análise e Desenvolvimento de Sistemas (ADS)")).findFirst().orElse(null);
        }

        if (cursoAds != null) {
            if (turmaRepository.findByCursoId(cursoAds.getId()).isEmpty()) {
                Turma turma1 = new Turma(null, "Turma A", cursoAds);
                Turma turma2 = new Turma(null, "Turma B", cursoAds);
                turmaRepository.save(turma1);
                turmaRepository.save(turma2);
                System.out.println("Turmas A e B criadas para o curso ADS com sucesso!");
            }

            if (!userRepository.existsByEmail("aluno@teste.com")) {
                Turma turma = turmaRepository.findByCursoId(cursoAds.getId()).get(0);
                User aluno = User.builder()
                        .nome("João da Silva")
                        .email("aluno@teste.com")
                        .senha(passwordEncoder.encode("aluno123"))
                        .role(Role.ALUNO)
                        .turma(turma)
                        .build();
                userRepository.save(aluno);
                System.out.println("Usuário ALUNO criado com sucesso! (aluno@teste.com / aluno123)");
            }

            if (regraRepository.findByCursoId(cursoAds.getId()).isEmpty()) {
                Regra regra = new Regra();
                regra.setCurso(cursoAds);
                regra.setTipo("Extensão");
                regra.setGrupo("Grupo 1");
                regra.setDescricao("Palestras e Eventos");
                regra.setAproveitamento("100%");
                regra.setRequisito("Certificado de participação");
                regraRepository.save(regra);
                System.out.println("Regra 'Palestras e Eventos' criada com sucesso!");
            }
        }
    }
}
