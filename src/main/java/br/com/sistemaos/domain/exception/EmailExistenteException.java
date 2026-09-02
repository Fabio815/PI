package br.com.sistemaos.domain.exception;

import br.com.sistemaos.infraestrura.exception.RequestException;

public class EmailExistenteException extends RequestException {
    public EmailExistenteException(String email) {
        super("Email existente: " + email, "emailExistente");
    }
}
