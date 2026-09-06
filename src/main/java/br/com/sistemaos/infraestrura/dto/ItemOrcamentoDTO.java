package br.com.sistemaos.infraestrura.dto;

import br.com.sistemaos.domain.entity.ItemOrcamento;
import br.com.sistemaos.domain.entity.Orcamento;
import br.com.sistemaos.domain.entity.Peca;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class ItemOrcamentoDTO {
    private final Long id;
    private final int quantidade;
    private final Double valorUnitario;
    private final Double valorTotal;
    private final PecaDTO peca;

    public static ItemOrcamentoDTO criar(ItemOrcamento itemOrcamento) {
        PecaDTO pecaDTO = PecaDTO.builder()
                .id(itemOrcamento.getItem().getId())
                .nome(itemOrcamento.getItem().getNome())
                .quantidade(itemOrcamento.getItem().getQuantidade())
                .preco(itemOrcamento.getItem().getPreco())
                .status(itemOrcamento.getItem().getStatus())
                .build();
        return new ItemOrcamentoDTO(
                itemOrcamento.getId(),
                itemOrcamento.getQuantidade(),
                itemOrcamento.getValorUnitario(),
                itemOrcamento.getValorTotal(),
                pecaDTO
        );

    }
}
