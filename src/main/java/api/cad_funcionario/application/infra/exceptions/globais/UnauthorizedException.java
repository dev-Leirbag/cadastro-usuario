package api.cad_funcionario.application.infra.exceptions.globais;

public class UnauthorizedException extends RuntimeException {
    public UnauthorizedException(String message) {
        super(message);
    }

    public UnauthorizedException(){
        super("Voce não tem permissão para fazer isso");
    }
}
