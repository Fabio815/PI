package br.com.sistemaos.domain.exception;

import br.com.sistemaos.infraestrura.exception.RequestException;

public class NumeroTelefoneExistenteException extends RequestException {
    public NumeroTelefoneExistenteException(String telefone) {
        super("Número de telefone já existente " + telefone, "telefoneExistente");
    }
}
