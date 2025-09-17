package api.cad_funcionario.application.infra.exceptions.cadastro;

public class EmailException extends RuntimeException {

    public EmailException(){
        super("O email não pode ser vazio!");
    }

    public EmailException(String message){
        super(message);
    }

}
