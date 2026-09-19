package br.com.sistemaos.domain.exception;

public class UsuarioNaoAutenticadoException extends RuntimeException {
    public UsuarioNaoAutenticadoException() {
        super("Usuário não autenticado");
    }
}