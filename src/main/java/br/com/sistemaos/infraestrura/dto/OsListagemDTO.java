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
    private final Long usuarioId;
    private final String usuarioNome;
    private final Long responsavelId;
    private final String responsavelNome;
    private final LocalDate dataFim;

    public OsListagemDTO(Long id, LocalDate dataEmissao, String nomeCliente, String telefone,
                         Double valorTotal, StatusOs situacao, Status status,
                         Long usuarioId, String usuarioNome) {
        this(id, dataEmissao, nomeCliente, telefone, valorTotal, situacao, status,
                usuarioId, usuarioNome, usuarioId, usuarioNome, null);
    }
}
