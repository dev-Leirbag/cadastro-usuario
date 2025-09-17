package api.cad_funcionario.application.infra.exceptions.login;

public class EmailNotFoundExeption extends RuntimeException {
    public EmailNotFoundExeption(String message) {
        super(message);
    }

    public EmailNotFoundExeption(){
        super("Não existe um usuário com esse email");
    }
}
