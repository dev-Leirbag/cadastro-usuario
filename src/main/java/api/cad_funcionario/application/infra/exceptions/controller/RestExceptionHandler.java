package api.cad_funcionario.application.infra.exceptions.controller;

import api.cad_funcionario.application.infra.exceptions.cadastro.EmailException;
import api.cad_funcionario.application.infra.exceptions.cadastro.EmailExistsException;
import api.cad_funcionario.application.infra.exceptions.cadastro.SenhaException;
import api.cad_funcionario.application.infra.exceptions.globais.UnauthorizedException;
import api.cad_funcionario.application.infra.exceptions.login.EmailNotFoundExeption;
import api.cad_funcionario.application.infra.exceptions.login.SenhaIncorretException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class RestExceptionHandler {

    private ResponseEntity<RestErrorMessage> mensagemErro(Exception exception, HttpStatus status) {
        RestErrorMessage errorMessage = new RestErrorMessage(status, status.value(), exception.getMessage());
        return new ResponseEntity<>(errorMessage, status);
    }

    @ExceptionHandler(EmailException.class)
    public ResponseEntity<RestErrorMessage> emailVazio(EmailException exception) {
        return mensagemErro(exception, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(EmailExistsException.class)
    public ResponseEntity<RestErrorMessage> emailJáExiste(EmailExistsException exception) {
        return mensagemErro(exception, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(SenhaException.class)
    public ResponseEntity<RestErrorMessage> senhaVazia(SenhaException exception) {
        return mensagemErro(exception, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(EmailNotFoundExeption.class)
    public ResponseEntity<RestErrorMessage> emailNaoExiste(EmailNotFoundExeption exception) {
        return mensagemErro(exception, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(SenhaIncorretException.class)
    public ResponseEntity<RestErrorMessage> senhaIncorreta(SenhaIncorretException exception){
        return mensagemErro(exception, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<RestErrorMessage> permissaoNegada(UnauthorizedException exception){
        return mensagemErro(exception, HttpStatus.UNAUTHORIZED);
    }
}
