package br.com.sistemaos.infraestrura.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SalvarItemOrcamentoDTO {   // também ajustei o nome, ver ponto 5
    @NotNull(message = "Quantidade não pode ser nula")
    @Min(value = 1, message = "Quantidade deve ser maior que zero")
    private final int quantidade;

    @NotNull(message = "Valor não pode ser nulo")
    @Min(value = 0)
    private final Double valorUnitario;

    @NotNull(message = "Peça é obrigatória")
    private final Long pecaId;   // ✅
}