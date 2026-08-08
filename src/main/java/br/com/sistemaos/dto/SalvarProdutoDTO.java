package br.com.sistemaos.dto;

import br.com.sistemaos.domain.model.Status;
import lombok.Data;

@Data
public class SalvarProdutoDTO {
    private String nome;
    private int quantidade;
    private Double preco;
    private Status status;
}
