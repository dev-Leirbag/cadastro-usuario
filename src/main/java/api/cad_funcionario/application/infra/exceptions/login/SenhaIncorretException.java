package api.cad_funcionario.application.infra.exceptions.login;

public class SenhaIncorretException extends RuntimeException {
    public SenhaIncorretException(String message) {
        super(message);
    }

    public SenhaIncorretException(){
        super("A senha está incorreta. Tente Novamente!!");
    }
}
