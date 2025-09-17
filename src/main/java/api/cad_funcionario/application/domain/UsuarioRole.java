package api.cad_funcionario.application.domain;

public enum UsuarioRole {
    ADMIN("admin"),
    FINANCEIRO("financeiro"),
    GERENTE("gerente"),
    VENDEDOR("vendedor");

    private String role;

    UsuarioRole(String role) {
        this.role = role;
    }

    public String getRole() {
        return role;
    }
}
