package br.com.sistemaos.infraestrura.dto;

import br.com.sistemaos.domain.entity.Cliente;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SalvarEnderecoDTO {
    @Size(max = 50, message = "Rua inválida")
    private String rua;

    @Size(max = 20, message = "Número inválido")
    private String numero;

    @Size(max = 80, message = "Logradouro inválido")
    private String logradouro;

    @Size(max = 200, message = "Complemento inválido")
    private String complemento;
}
