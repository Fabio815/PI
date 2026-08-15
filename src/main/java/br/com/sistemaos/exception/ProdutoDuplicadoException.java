package br.com.sistemaos.exception;

import br.com.sistemaos.dto.SalvarProdutoDTO;

public class ProdutoDuplicadoException extends RequestException{
    public ProdutoDuplicadoException(String produtoDTO) {
        super("Produto já existente" + produtoDTO, "produtoexistente");
    }
}
