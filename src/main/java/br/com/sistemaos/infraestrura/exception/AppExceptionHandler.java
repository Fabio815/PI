package br.com.sistemaos.infraestrura.exception;

import br.com.sistemaos.domain.exception.AcessoNegadoException;
import br.com.sistemaos.domain.exception.ConflitoOperacionalException;
import br.com.sistemaos.domain.exception.OsNaoEncontradaException;
import br.com.sistemaos.domain.exception.RecursoNaoEncontradoException;
import org.jspecify.annotations.Nullable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import jakarta.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.List;

@ControllerAdvice
public class AppExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handlerGenericException(Exception ex, WebRequest request) {
        return handlerException(ex, null, ex.getMessage(), null, HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    @ExceptionHandler(RequestException.class)
    public ResponseEntity<Object> handlerRequestException(RequestException ex, WebRequest request) {
        return handlerException(ex, ex.getCodigoErro(), ex.getMessage(), null, HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(AcessoNegadoException.class)
    public ResponseEntity<Object> handlerAcessoNegadoException(AcessoNegadoException ex, WebRequest request) {
        return handlerException(ex, ex.getCodigoErro(), ex.getMessage(), null, HttpStatus.FORBIDDEN, request);
    }

    @ExceptionHandler({OsNaoEncontradaException.class, RecursoNaoEncontradoException.class})
    public ResponseEntity<Object> handlerNaoEncontrado(RuntimeException ex, WebRequest request) {
        return handlerException(ex, "Recurso não encontrado", ex.getMessage(), null, HttpStatus.NOT_FOUND, request);
    }

    @ExceptionHandler(ConflitoOperacionalException.class)
    public ResponseEntity<Object> handlerConflito(ConflitoOperacionalException ex, WebRequest request) {
        return handlerException(ex, "Conflito operacional", ex.getMessage(), null, HttpStatus.CONFLICT, request);
    }

    @ExceptionHandler({MethodArgumentTypeMismatchException.class, ConstraintViolationException.class, IllegalArgumentException.class})
    public ResponseEntity<Object> handlerEntradaInvalida(Exception ex, WebRequest request) {
        return handlerException(ex, "Erro de validação", ex.getMessage(), null, HttpStatus.BAD_REQUEST, request);
    }

    @Override
    protected @Nullable ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex,
            HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        return handlerException(ex, "Requisição inválida", "Corpo JSON ausente, malformado ou com valores inválidos.",
                null, HttpStatus.BAD_REQUEST, request);
    }

    @Override
    protected @Nullable ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
            HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        List<String> erros = new ArrayList<>();
        for (ObjectError erro : ex.getBindingResult().getAllErrors()) erros.add(erro.getDefaultMessage());
        return handlerException(ex, "Erro de validação", null, erros, HttpStatus.BAD_REQUEST, request);
    }

    public ResponseEntity<Object> handlerException(Exception ex, String codigoErro, String mensagemErro,
            List<String> detalhes, HttpStatus statusErro, WebRequest request) {
        ServletWebRequest webRequest = (ServletWebRequest) request;
        return handleExceptionInternal(ex, RestError.builder().codigoErro(codigoErro).mensagemErro(mensagemErro)
                        .status(statusErro.value()).descricaoErro(detalhes)
                        .caminho(webRequest.getRequest().getRequestURI()).build(),
                new HttpHeaders(), statusErro, request);
    }
}
