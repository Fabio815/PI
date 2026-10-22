package br.com.sistemaos.infraestrura.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SalvarComentarioDTO {
    @NotBlank(message = "Comentário não pode ficar vazio")
    @Size(max = 4000, message = "Comentário deve ter no máximo 4000 caracteres")
    private String conteudo;
}
