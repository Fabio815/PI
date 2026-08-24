package br.com.sistemaos.infraestrura.dto;

import br.com.sistemaos.domain.entity.Cliente;
import br.com.sistemaos.domain.model.Status;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class SalvarClienteDTO {
    @NotNull(message = "O nome do cliente não pode ser vazio")
    private String nome;

    @NotNull(message = "O telefone não pode ser vazio")
    @Size(min = 1, max = 15, message = "Telefone maior que 14 caracteres")
    private String telefone;

    private Status status;
}
