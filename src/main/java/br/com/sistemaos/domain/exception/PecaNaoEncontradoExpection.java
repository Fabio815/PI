package br.com.sistemaos.domain.exception;

import br.com.sistemaos.infraestrura.exception.RequestException;

public class PecaNaoEncontradoExpection extends RequestException {
    public PecaNaoEncontradoExpection(Long id) {
        super("Produto não encontrado" + id, "produtonaoencontrado");
    }
}
