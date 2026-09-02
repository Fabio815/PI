package br.com.sistemaos.infraestrura.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class SalvarEnderecoDTO {
    @Size(max = 50, message = "Rua inválida")
    private final String rua;

    @Size(max = 20, message = "Número inválido")
    private final String numero;

    @Size(max = 80, message = "Logradouro inválido")
    private final String logradouro;

    @Size(max = 200, message = "Complemento inválido")
    private final String complemento;
}
