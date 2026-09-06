package br.com.sistemaos.infraestrura.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class SalvarOsDTO {
    @NotNull(message = "Cliente é obrigatório")
    private final Long clienteId;

    private final Long usuarioId;

    @NotNull(message = "Orçamento é obrigatório")
    private final SalvarOrcamentoDTO orcamento;
}