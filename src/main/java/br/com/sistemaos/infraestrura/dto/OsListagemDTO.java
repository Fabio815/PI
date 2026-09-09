package br.com.sistemaos.infraestrura.dto;

import br.com.sistemaos.domain.model.Status;
import br.com.sistemaos.domain.model.StatusOs;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
@Builder
public class OsListagemDTO {
    private final Long id;
    private final LocalDate dataEmissao;
    private final String nomeCliente;
    private final String telefone;
    private final Double valorTotal;
    private final StatusOs situacao;
    private final Status status;
}
