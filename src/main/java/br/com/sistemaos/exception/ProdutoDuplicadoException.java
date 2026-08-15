package br.com.sistemaos.exception;

public class ProdutoDuplicadoException extends RequestException{
    public ProdutoDuplicadoException(String nome) {
        super("Produto já existente" + nome, "produtoduplicado");
    }
}
