package br.com.sistemaos.infraestrura.dto;

import br.com.sistemaos.domain.model.Status;
import lombok.Data;

@Data
public class SalvarPecaDTO {
    private String nome;
    private int quantidade;
    private Double preco;
    private Status status;
}
