package br.com.sistemaos.domain.entity;

import br.com.sistemaos.domain.model.StatusOs;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Os {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "data_emissao", nullable = false)
    private LocalDate dataEmissao;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private StatusOs status;

    @ManyToOne
    @JoinColumn(name = "id_cliente")
    private Cliente cliente;

    @OneToOne(mappedBy = "os", cascade = CascadeType.ALL, orphanRemoval = true)
    private Orcamento orcamento;
}
