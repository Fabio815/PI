package br.com.sistemaos.exception;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RestError {
    private final String codigo;
    private final String mensagem;
    private final int statusRequisicao;
    private final String caminho;
    private final boolean status;
}
