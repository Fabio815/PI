package br.com.sistemaos.domain.exception;

import lombok.Data;
import lombok.Getter;

@Getter
public class RequestException extends RuntimeException {
    private final String codigoErro;

    public RequestException(String mensagem, String codigoErro) {
        super(mensagem);
        this.codigoErro = codigoErro;
    }
}