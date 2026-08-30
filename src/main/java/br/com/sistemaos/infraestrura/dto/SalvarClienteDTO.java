package br.com.sistemaos.infraestrura.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SalvarClienteDTO {
    @NotBlank(message = "O nome do cliente não pode estar vazio")
    private String nome;

    @NotNull(message = "O telefone não pode ser vazio")
    @Size(min = 1, max = 15, message = "Telefone maior que 14 caracteres")
    private String telefone;

    private String status;

    private SalvarEnderecoDTO endereco;
}
