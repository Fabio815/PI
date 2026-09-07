package br.com.sistemaos.infraestrura.dto;

import br.com.sistemaos.domain.model.StatusOs;

import java.time.LocalDate;

public class OsListagemDTO {
    private Long id;
    private LocalDate dataEmissao;
    private String nomeCliente;
    private String telefone;
    private Double valorTotal;
    private StatusOs status;
    private boolean ativo;
}
