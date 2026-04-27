package com.gerenciamento.certificado.entity;

import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "users")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String senha;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "curso_id")
    private Curso curso;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "turma_id")
    private Turma turma;

    @Column(name = "codigo_recuperacao")
    private String codigoRecuperacao;

    @Column(name = "codigo_recuperacao_expira_em")
    private java.time.LocalDateTime codigoRecuperacaoExpiraEm;

    public User() {}

    public User(Long id, String nome, String email, String senha, Role role, Curso curso, Turma turma) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.role = role;
        this.curso = curso;
        this.turma = turma;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    public Curso getCurso() { return curso; }
    public void setCurso(Curso curso) { this.curso = curso; }

    public Turma getTurma() { return turma; }
    public void setTurma(Turma turma) { this.turma = turma; }

    public String getCodigoRecuperacao() { return codigoRecuperacao; }
    public void setCodigoRecuperacao(String codigoRecuperacao) { this.codigoRecuperacao = codigoRecuperacao; }

    public java.time.LocalDateTime getCodigoRecuperacaoExpiraEm() { return codigoRecuperacaoExpiraEm; }
    public void setCodigoRecuperacaoExpiraEm(java.time.LocalDateTime codigoRecuperacaoExpiraEm) { this.codigoRecuperacaoExpiraEm = codigoRecuperacaoExpiraEm; }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override
    public String getPassword() {
        return senha;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public static UserBuilder builder() { return new UserBuilder(); }

    public static class UserBuilder {
        private Long id;
        private String nome;
        private String email;
        private String senha;
        private Role role;
        private Curso curso;
        private Turma turma;

        public UserBuilder id(Long id) { this.id = id; return this; }
        public UserBuilder nome(String nome) { this.nome = nome; return this; }
        public UserBuilder email(String email) { this.email = email; return this; }
        public UserBuilder senha(String senha) { this.senha = senha; return this; }
        public UserBuilder role(Role role) { this.role = role; return this; }
        public UserBuilder curso(Curso curso) { this.curso = curso; return this; }
        public UserBuilder turma(Turma turma) { this.turma = turma; return this; }

        public User build() {
            return new User(id, nome, email, senha, role, curso, turma);
        }
    }
}
