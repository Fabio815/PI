package br.com.sistemaos.domain.exception;

import br.com.sistemaos.infraestrura.exception.RequestException;

public class ProdutoDuplicadoException extends RequestException {
    public ProdutoDuplicadoException(String nome) {
        super("Produto já existente" + nome, "produtoduplicado");
    }
}
