package br.com.sistemaos.domain.exception;

import br.com.sistemaos.infraestrura.exception.RequestException;

public class PecaDuplicadoException extends RequestException {
    public PecaDuplicadoException(String nome) {
        super("Produto já existente" + nome, "produtoduplicado");
    }
}
