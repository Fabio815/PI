package br.com.sistemaos.infraestrura.dto;

import br.com.sistemaos.domain.entity.Orcamento;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Optional;

@Data
@Builder
@AllArgsConstructor
public class OrcamentoDTO {
    private final Long id;
    private final Double valorServico;
    private final Double valorTotal;
    private final String observacoes;
    private final List<ItemOrcamentoDTO> itens;

    public static OrcamentoDTO criar(Orcamento orcamento) {
        return new OrcamentoDTO(
                orcamento.getId(),
                orcamento.getValorServico(),
                orcamento.getValorTotal(),
                orcamento.getObservacoes(),
                Optional.ofNullable(orcamento.getItemOrcamento())
                        .orElse(List.of())
                        .stream()
                        .map(ItemOrcamentoDTO::criar)
                        .toList()
        );
    }
}
