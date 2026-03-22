package br.com.sistemaos.domain.model;

public enum Status {
    ATIVO(1),
    INATIVO(2);

    private Integer codigo;
    private Status(Integer codigo) {
        this.codigo = codigo;
    }
}
