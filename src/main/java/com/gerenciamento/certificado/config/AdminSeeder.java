package com.gerenciamento.certificado.config;

import com.gerenciamento.certificado.entity.Curso;
import com.gerenciamento.certificado.entity.Role;
import com.gerenciamento.certificado.entity.User;
import com.gerenciamento.certificado.repository.CursoRepository;
import com.gerenciamento.certificado.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AdminSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final CursoRepository cursoRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminSeeder(UserRepository userRepository, CursoRepository cursoRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.cursoRepository = cursoRepository;
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

        if (!cursoRepository.existsByNome("Análise e Desenvolvimento de Sistemas (ADS)")) {
            cursoRepository.save(new Curso(null, "Análise e Desenvolvimento de Sistemas (ADS)"));
            System.out.println("Curso ADS criado com sucesso!");
        }
    }
}
