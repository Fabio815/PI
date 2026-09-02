package br.com.sistemaos.infraestrura.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class EnderecoDTO {
    private final Long id;
    private final String rua;
    private final String numero;
    private final String logradouro;
    private final String complemento;
}
