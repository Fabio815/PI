package br.com.sistemaos.infraestrura.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class SalvarOrcamentoDTO {
    @NotNull(message = "Mão de obra é obrigatória")
    @DecimalMin(value = "0.00", inclusive = true, message = "Mão de obra não pode ser negativa")
    @Digits(integer = 10, fraction = 2, message = "Mão de obra deve possuir no máximo duas casas decimais")
    private Double valorServico;

    @Size(max = 100, message = "Observação inválida")
    private String observacoes;

    @Valid
    @NotEmpty(message = "Orçamento precisa ter ao menos um item")
    private final List<SalvarItemOrcamentoDTO> itens;
}