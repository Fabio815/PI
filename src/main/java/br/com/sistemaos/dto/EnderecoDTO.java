package br.com.sistemaos.dto;

import br.com.sistemaos.domain.entity.Cliente;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EnderecoDTO {
    private Long id;
    private String rua;
    private String numero;
    private String logradouro;
    private String complemento;
}
