package br.com.sistemaos.infraestrura.dto;

import br.com.sistemaos.domain.entity.Produto;
import br.com.sistemaos.domain.model.Status;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProdutoDTO {
    private Long id;
    private String nome;
    private int quantidade;
    private Double preco;
    private Status status;

    public static ProdutoDTO criar(Produto produto) {
        return new ProdutoDTO(
                produto.getId(),
                produto.getNome(),
                produto.getQuantidade(),
                produto.getPreco(),
                produto.getStatus()
        );
    }
}
