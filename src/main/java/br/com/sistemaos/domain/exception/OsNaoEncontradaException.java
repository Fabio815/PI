package br.com.sistemaos.domain.exception;

import br.com.sistemaos.infraestrura.exception.RequestException;

public class OsNaoEncontradaException extends RequestException {
    public OsNaoEncontradaException(Long id) {
        super("Os não encontrada " + id, "osNaoEncontrada");
    }
}