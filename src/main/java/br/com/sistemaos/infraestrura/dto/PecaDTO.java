package br.com.sistemaos.infraestrura.dto;

import br.com.sistemaos.domain.entity.Peca;
import br.com.sistemaos.domain.model.Status;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PecaDTO {
    private final Long id;
    private final String nome;
    private final int quantidade;
    private final Double preco;
    private final Status status;

    public static PecaDTO criar(Peca peca) {
        return new PecaDTO(
                peca.getId(),
                peca.getNome(),
                peca.getQuantidade(),
                peca.getPreco(),
                peca.getStatus()
        );
    }
}
