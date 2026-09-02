package br.com.sistemaos.infraestrura.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SalvarClienteDTO {
    @NotBlank(message = "O nome do cliente não pode estar vazio")
    private final String nome;

    @NotNull(message = "O telefone não pode ser vazio")
    @Size(min = 1, max = 15, message = "Telefone maior que 14 caracteres")
    private final String telefone;

    private final String status;

    private final SalvarEnderecoDTO endereco;
}
