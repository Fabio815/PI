package br.com.sistemaos.infraestrura.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
