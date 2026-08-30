package br.com.sistemaos.domain.exception;

import br.com.sistemaos.infraestrura.exception.RequestException;

public class ConverteStatusException extends RequestException {
    public ConverteStatusException(String status) {
        super("Erro ao converter status " + status, "erroAoConverterStatus");
    }
}
