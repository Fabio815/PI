package br.com.sistemaos.infraestrura.dto;

import br.com.sistemaos.domain.model.StatusOs;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AtualizarSituacaoOsDTO {
    @NotNull(message = "Situação é obrigatória")
    private StatusOs situacao;
}
