package api.cad_funcionario.application.infra.exceptions.cadastro;

public class SenhaMinSizeException extends RuntimeException {
    public SenhaMinSizeException(String message) {
        super(message);
    }

    public SenhaMinSizeException(){
        super("A senha deve conter no mínimo 6 caracteres");
    }
}
