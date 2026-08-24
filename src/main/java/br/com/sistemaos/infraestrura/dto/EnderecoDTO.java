package br.com.sistemaos.infraestrura.dto;

import br.com.sistemaos.domain.entity.Cliente;
import jakarta.persistence.*;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EnderecoDTO {
    private Long id;
    private String rua;
    private String numero;
    private String logradouro;
    private String complemento;
}
