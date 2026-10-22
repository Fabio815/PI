package br.com.sistemaos.infraestrura.dto;

import br.com.sistemaos.domain.model.StatusOs;

public record RelatorioOsPorStatusDTO(StatusOs situacao, long total) {
}
