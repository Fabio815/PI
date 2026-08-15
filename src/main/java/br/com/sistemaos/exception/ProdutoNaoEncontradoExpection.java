package br.com.sistemaos.exception;

public class ProdutoNaoEncontradoExpection extends RequestException{
    public ProdutoNaoEncontradoExpection(Long id) {
        super("Produto não encontrado" + id, "produtonaoencontrado");
    }
}
