package br.com.sistemaos.infraestrura.dto;

import br.com.sistemaos.domain.model.StatusOs;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class SalvarOsDTO {
    @NotNull(message = "Cliente é obrigatório")
    private final Long clienteId;

    private final String modelo;

    private final String cor;

    private final StatusOs situacao;

    @Valid
    @NotNull(message = "Orçamento é obrigatório")
    private final SalvarOrcamentoDTO orcamento;
}