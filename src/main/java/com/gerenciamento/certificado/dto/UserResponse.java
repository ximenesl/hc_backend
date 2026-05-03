package com.gerenciamento.certificado.dto;

import com.gerenciamento.certificado.entity.Role;

public class UserResponse {
    private Long id;
    private String nome;
    private String email;
    private Role role;
    private java.util.List<CursoResponse> cursos;
    private TurmaResponse turma;
    private Integer horasAprovadas;
    private Boolean ativo;


    public UserResponse() {}

    public UserResponse(Long id, String nome, String email, Role role, java.util.List<CursoResponse> cursos, TurmaResponse turma, Integer horasAprovadas) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.role = role;
        this.cursos = cursos;
        this.turma = turma;
        this.horasAprovadas = horasAprovadas;
        this.ativo = true; // default, will be overridden by builder if provided
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }
    public java.util.List<CursoResponse> getCursos() { return cursos; }
    public void setCursos(java.util.List<CursoResponse> cursos) { this.cursos = cursos; }
    public TurmaResponse getTurma() { return turma; }
    public void setTurma(TurmaResponse turma) { this.turma = turma; }
    public Integer getHorasAprovadas() { return horasAprovadas; }
    public void setHorasAprovadas(Integer horasAprovadas) { this.horasAprovadas = horasAprovadas; }
    public Boolean getAtivo() { return ativo; }
    public void setAtivo(Boolean ativo) { this.ativo = ativo; }


    public static UserResponseBuilder builder() { return new UserResponseBuilder(); }

    public static class UserResponseBuilder {
        private Long id;
        private String nome;
        private String email;
        private Role role;
        private java.util.List<CursoResponse> cursos;
        private TurmaResponse turma;
        private Integer horasAprovadas;
        private Boolean ativo;


        public UserResponseBuilder id(Long id) { this.id = id; return this; }
        public UserResponseBuilder nome(String nome) { this.nome = nome; return this; }
        public UserResponseBuilder email(String email) { this.email = email; return this; }
        public UserResponseBuilder role(Role role) { this.role = role; return this; }
        public UserResponseBuilder cursos(java.util.List<CursoResponse> cursos) { this.cursos = cursos; return this; }
        public UserResponseBuilder turma(TurmaResponse turma) { this.turma = turma; return this; }
        public UserResponseBuilder horasAprovadas(Integer horasAprovadas) { this.horasAprovadas = horasAprovadas; return this; }
        public UserResponseBuilder ativo(Boolean ativo) { this.ativo = ativo; return this; }


        public UserResponse build() {
            UserResponse resp = new UserResponse(id, nome, email, role, cursos, turma, horasAprovadas);
            resp.setAtivo(this.ativo);
            return resp;
        }
    }
}
