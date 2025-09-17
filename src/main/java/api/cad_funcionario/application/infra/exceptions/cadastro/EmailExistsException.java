package api.cad_funcionario.application.infra.exceptions.cadastro;

public class EmailExistsException extends RuntimeException {
    public EmailExistsException(String message) {
        super(message);
    }

    public EmailExistsException(){
        super("Esse email já esta cadastrado");
    }
}
