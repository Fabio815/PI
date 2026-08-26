package br.com.sistemaos.domain.exception;

public class ClienteNaoEncontradoException extends RequestException {
    public ClienteNaoEncontradoException(Long id) {
        super("Cliente não encontrado " + id, "clienteNãoEncontrado");
    }
}
