package br.com.sistemaos.domain.exception;

public class ProdutoNaoEncontradoExpection extends RequestException{
    public ProdutoNaoEncontradoExpection(Long id) {
        super("Produto não encontrado" + id, "produtonaoencontrado");
    }
}
