package br.com.sistemaos.infraestrura.dto;

import br.com.sistemaos.domain.model.Perfil;
import br.com.sistemaos.domain.model.Status;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class SalvarUsuarioDTO {
    @NotNull(message = "Nome não pode ser nulo")
    @Size(min = 1, max = 50)
    private String nome;

    @NotNull(message = "Email não pode ser nulo")
    @Size(min = 1, max = 80)
    private String email;

    @NotNull(message = "Senha não pode ser nulo")
    private String senha;

    @NotNull(message = "Chave não pode ser nulo")
    private Perfil chave;

    private Status status;
}
