package com.gerenciamento.certificado.dto;

import com.gerenciamento.certificado.entity.Role;

public class UserResponse {
    private Long id;
    private String nome;
    private String email;
    private Role role;
    private CursoResponse curso;

    public UserResponse() {}

    public UserResponse(Long id, String nome, String email, Role role, CursoResponse curso) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.role = role;
        this.curso = curso;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }
    public CursoResponse getCurso() { return curso; }
    public void setCurso(CursoResponse curso) { this.curso = curso; }

    public static UserResponseBuilder builder() { return new UserResponseBuilder(); }

    public static class UserResponseBuilder {
        private Long id;
        private String nome;
        private String email;
        private Role role;
        private CursoResponse curso;

        public UserResponseBuilder id(Long id) { this.id = id; return this; }
        public UserResponseBuilder nome(String nome) { this.nome = nome; return this; }
        public UserResponseBuilder email(String email) { this.email = email; return this; }
        public UserResponseBuilder role(Role role) { this.role = role; return this; }
        public UserResponseBuilder curso(CursoResponse curso) { this.curso = curso; return this; }

        public UserResponse build() {
            return new UserResponse(id, nome, email, role, curso);
        }
    }
}
