package br.com.sistemaos.infraestrura.dto;

import java.util.List;
import java.util.Map;

public record RelatorioOsRespostaDTO(
        List<RelatorioOsLinhaDTO> linhas,
        long total,
        List<RelatorioOsPorStatusDTO> porStatus,
        List<RelatorioOsPorPeriodoDTO> porPeriodo,
        Map<String, Object> filtros
) {
}
