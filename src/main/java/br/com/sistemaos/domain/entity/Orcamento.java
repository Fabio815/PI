package br.com.sistemaos.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Orcamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "valor_servico")
    private Double valorServico;

    @Column(name = "valor_total")
    private Double valorTotal;

    @Column(name = "observacoes", length = 100)
    private String observacoes;

    @OneToOne
    @JoinColumn(name = "id_os")
    private Os os;

    @OneToMany(mappedBy = "orcamento", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemOrcamento> itens;
}
