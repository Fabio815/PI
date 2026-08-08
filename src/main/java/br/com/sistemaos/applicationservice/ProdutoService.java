package br.com.sistemaos.applicationservice;

import br.com.sistemaos.domain.entity.Produto;
import br.com.sistemaos.domain.model.ProdutoRepository;
import br.com.sistemaos.domain.model.Status;
import br.com.sistemaos.dto.SalvarProdutoDTO;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ProdutoService {
    private final ProdutoRepository produtoRepository;

    public Produto criarProduto(SalvarProdutoDTO salvarProdutoDTO) {
        Produto produto = Produto.builder()
                .nome(salvarProdutoDTO.getNome())
                .quantidade(salvarProdutoDTO.getQuantidade())
                .preco(salvarProdutoDTO.getPreco())
                .status(Status.ATIVO)
                .build();

        produtoRepository.save(produto);
        return null;
    }
}
