package api.cad_funcionario.application.infra.exceptions.cadastro;

public class SenhaException extends RuntimeException {

    public SenhaException(){
        super("A senha não pode estar vazia");
    }

    public SenhaException(String mensage){
        super(mensage);
    }
}
