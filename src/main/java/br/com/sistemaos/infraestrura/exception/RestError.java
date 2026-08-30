package br.com.sistemaos.infraestrura.exception;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class RestError {
    private final String codigoErro;
    private final String mensagemErro;
    private final List<String> descricaoErro;
    private final int status;
    private final String caminho;
}
