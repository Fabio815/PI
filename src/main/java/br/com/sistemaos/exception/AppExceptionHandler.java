package br.com.sistemaos.exception;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@ControllerAdvice
public class AppExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler (value = RequestException.class)
    public ResponseEntity<Object> execaoPorRequisicao(RequestException ex, WebRequest request) {
        return handlerException(ex, ex.getErrorCode(), ex.getMessage(), BAD_REQUEST, request);
    }

    @ExceptionHandler (value = Exception.class)
    public ResponseEntity<Object> excecaoGenerica(Exception ex, WebRequest request) {
        return handlerException(ex, null, ex.getMessage(), INTERNAL_SERVER_ERROR, request);
    }

    private ResponseEntity<Object> handlerException(Exception ex, String codigoErro, String mensagem, HttpStatus status, WebRequest request) {
        ServletWebRequest caminhoDaRequisicao = (ServletWebRequest) request;

        return handleExceptionInternal(
                ex,
                RestError
                        .builder()
                        .codigo(codigoErro)
                        .mensagem(mensagem)
                        .status(status.value())
                        .caminho(caminhoDaRequisicao.getRequest().getRequestURI())
                        .build(),
                new HttpHeaders(),
                status,
                request
        );
    }
}
