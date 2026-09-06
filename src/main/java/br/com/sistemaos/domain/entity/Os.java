package br.com.sistemaos.domain.entity;

import br.com.sistemaos.domain.model.StatusOs;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "os")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Os {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "data_emissao", nullable = false)
    private LocalDate dataEmissao;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private StatusOs status;

    //relacionamento
    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "orcamento_id", unique = true)
    private Orcamento orcamento;
}
