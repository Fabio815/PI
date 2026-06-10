package br.com.sistemaos.domain.model;

import lombok.Data;

@Data
public class Filtro {
    private String propriedade;
    private String operador;
    private Object valor;
}
