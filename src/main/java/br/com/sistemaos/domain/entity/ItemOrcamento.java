package br.com.sistemaos.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "item_orcamento")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ItemOrcamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "quantidade")
    private int quantidade;

    @Column(name = "valor_unitario")
    private Double valorUnitario;

    @Column(name = "valor_total")
    private Double valorTotal;

    //relacionamento
    @ManyToOne
    @JoinColumn(name = "orcamento_id")
    private Orcamento orcamento;

    @ManyToOne
    @JoinColumn(name = "peca_id")
    private Peca item;
}
