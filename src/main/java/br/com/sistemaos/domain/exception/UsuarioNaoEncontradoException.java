package br.com.sistemaos.domain.exception;

import br.com.sistemaos.infraestrura.exception.RequestException;

public class UsuarioNaoEncontradoException extends RequestException {
    public UsuarioNaoEncontradoException(Long id) {
        super("Usuário não encontrado " + id, "usuarioNaoEncontrado");
    }

    public UsuarioNaoEncontradoException(String email) {
        super("Usuário com email " + email + " não encontrado", "usuarioNaoEncontrado");
    }
}
