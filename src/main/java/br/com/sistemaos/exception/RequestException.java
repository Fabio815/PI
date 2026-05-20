package br.com.sistemaos.exception;

import lombok.Data;

@Data
public class RequestException extends RuntimeException {
    private final String errorCode;

    public RequestException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }
}