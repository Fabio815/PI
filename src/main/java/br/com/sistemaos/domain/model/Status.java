package br.com.sistemaos.domain.model;

import lombok.Getter;

@Getter
public enum Status {
    ATIVO("ATIVO"),
    INATIVO("INATIVO");

    private final String descricao;

    Status(String descricao) {
        this.descricao = descricao;
    }
}
