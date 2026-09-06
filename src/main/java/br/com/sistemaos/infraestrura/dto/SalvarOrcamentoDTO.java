package br.com.sistemaos.infraestrura.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class SalvarOrcamentoDTO {
    private Double valorServico;

    @Size(max = 100, message = "Observação inválida")
    private String observacoes;

    @NotEmpty(message = "Orçamento precisa ter ao menos um item")
    private final List<SalvarItemOrcamentoDTO> itens;
}