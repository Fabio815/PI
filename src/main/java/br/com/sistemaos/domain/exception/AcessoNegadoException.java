package br.com.sistemaos.domain.exception;

import br.com.sistemaos.infraestrura.exception.RequestException;

public class AcessoNegadoException extends RequestException {
    public AcessoNegadoException(String mensagem) {
        super(mensagem, "acessoNegado");
    }
}
