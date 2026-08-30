package br.com.sistemaos.infraestrura.exception;

import lombok.Getter;

@Getter
public class RequestException extends RuntimeException {
    private final String codigoErro;

    public RequestException(String mensagem, String codigoErro) {
        super(mensagem);
        this.codigoErro = codigoErro;
    }
}