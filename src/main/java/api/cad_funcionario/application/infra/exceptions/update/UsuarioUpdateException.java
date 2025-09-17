package api.cad_funcionario.application.infra.exceptions.update;

public class UsuarioUpdateException extends RuntimeException {
    public UsuarioUpdateException(String message) {
        super(message);
    }

    public UsuarioUpdateException(){
        super("Erro ao atualizar o usuario");
    }
}
