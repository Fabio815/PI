package br.com.sistemaos.infraestrura.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AtribuirResponsavelDTO {
    @NotNull(message = "Responsável é obrigatório")
    @Positive(message = "Responsável inválido")
    private Long responsavelId;
}
