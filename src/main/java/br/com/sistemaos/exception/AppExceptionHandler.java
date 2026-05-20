package br.com.sistemaos.exception;

import org.jspecify.annotations.Nullable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
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
    public ResponseEntity<Object> handlerRequestException(RequestException ex, WebRequest request) {
        return handlerException(ex, ex.getErrorCode(), ex.getMessage(), BAD_REQUEST, false, request);
    }

    @ExceptionHandler (value = Exception.class)
    public ResponseEntity<Object> handlerGenericException(Exception ex, WebRequest request) {
        return handlerException(ex, null, ex.getMessage(), INTERNAL_SERVER_ERROR, false, request);
    }

    @Override
    protected @Nullable ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        return super.handleMethodArgumentNotValid(ex, headers, status, request);
    }

    private ResponseEntity<Object> handlerException(Exception ex, String codigoErro, String mensagem, HttpStatus statusRequisicao, boolean status, WebRequest request) {
        ServletWebRequest caminhoDaRequisicao = (ServletWebRequest) request;

        return handleExceptionInternal(
                ex,
                RestError
                        .builder()
                        .codigo(codigoErro)
                        .mensagem(mensagem)
                        .statusRequisicao(statusRequisicao.value())
                        .caminho(caminhoDaRequisicao.getRequest().getRequestURI())
                        .status(status)
                        .build(),
                new HttpHeaders(),
                statusRequisicao,
                request
        );
    }
}
