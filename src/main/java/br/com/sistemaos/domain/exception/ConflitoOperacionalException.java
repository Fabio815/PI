package br.com.sistemaos.domain.exception;

public class ConflitoOperacionalException extends RuntimeException {
    public ConflitoOperacionalException(String mensagem) {
        super(mensagem);
    }
}
