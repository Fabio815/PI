package br.com.sistemaos.infraestrura.dto;

import br.com.sistemaos.domain.model.Status;
import br.com.sistemaos.domain.model.StatusOs;

import java.time.LocalDate;

/** Projeção segura da OS para a tela de relatório. */
public record RelatorioOsLinhaDTO(
        Long id,
        LocalDate dataEmissao,
        LocalDate dataFim,
        Long clienteId,
        String clienteNome,
        Long responsavelId,
        String responsavelNome,
        StatusOs situacao,
        Status status,
        Double valorTotal
) {
}
