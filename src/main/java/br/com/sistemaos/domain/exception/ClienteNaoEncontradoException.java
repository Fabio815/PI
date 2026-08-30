package br.com.sistemaos.domain.exception;

import br.com.sistemaos.infraestrura.exception.RequestException;

public class ClienteNaoEncontradoException extends RequestException {
    public ClienteNaoEncontradoException(Long id) {
        super("Cliente não encontrado " + id, "clienteNãoEncontrado");
    }
}
