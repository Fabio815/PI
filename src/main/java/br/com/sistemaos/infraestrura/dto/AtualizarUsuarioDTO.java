package br.com.sistemaos.infraestrura.dto;


import br.com.sistemaos.domain.model.Perfil;
import br.com.sistemaos.domain.model.Status;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class AtualizarUsuarioDTO {
    @NotNull(message = "Nome não pode ser nulo")
    @Size(min = 1, max = 50)
    private final String nome;

    @NotNull(message = "Email não pode ser nulo")
    @Size(min = 1, max = 80)

    private final String email;
    private final Perfil chave;
    private final Status status;
}
