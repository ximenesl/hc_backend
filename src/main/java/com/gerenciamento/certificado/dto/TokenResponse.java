package com.gerenciamento.certificado.dto;

public class TokenResponse {
    private String token;
    private Long id;
    private String nome;
    private String role;

    public TokenResponse() {}

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public static TokenResponseBuilder builder() { return new TokenResponseBuilder(); }

    public static class TokenResponseBuilder {
        private String token;
        private Long id;
        private String nome;
        private String role;

        public TokenResponseBuilder token(String token) { this.token = token; return this; }
        public TokenResponseBuilder id(Long id) { this.id = id; return this; }
        public TokenResponseBuilder nome(String nome) { this.nome = nome; return this; }
        public TokenResponseBuilder role(String role) { this.role = role; return this; }

        public TokenResponse build() {
            TokenResponse tr = new TokenResponse();
            tr.setToken(token);
            tr.setId(id);
            tr.setNome(nome);
            tr.setRole(role);
            return tr;
        }
    }
}
