package br.com.sistemaos.domain.exception;

import br.com.sistemaos.infraestrura.exception.RequestException;

public class ProdutoNaoEncontradoExpection extends RequestException {
    public ProdutoNaoEncontradoExpection(Long id) {
        super("Produto não encontrado" + id, "produtonaoencontrado");
    }
}
