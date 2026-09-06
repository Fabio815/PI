package br.com.sistemaos.infraestrura.dto;

import br.com.sistemaos.domain.model.Status;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SalvarPecaDTO {
    private final String nome;
    private final int quantidade;
    private final Double preco;
}
