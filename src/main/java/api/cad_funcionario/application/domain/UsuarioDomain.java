package api.cad_funcionario.application.domain;

import java.util.UUID;

public class UsuarioDomain {
    private UUID id;
    private String email;
    private String senha;
    private UsuarioRole role;

    public UsuarioDomain(UUID id, String email, String senha, UsuarioRole role) {
        this.id = id;
        this.email = email;
        this.senha = senha;
        this.role = role;
    }

    public UsuarioDomain() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public UsuarioRole getRole() {
        return role;
    }

    public void setRole(UsuarioRole role) {
        this.role = role;
    }
}
