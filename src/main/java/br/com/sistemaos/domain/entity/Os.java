package br.com.sistemaos.domain.entity;

import br.com.sistemaos.domain.model.Status;
import br.com.sistemaos.domain.model.StatusOs;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "os")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Os {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "data_emissao", nullable = false)
    private LocalDate dataEmissao;

    @Column(name = "modelo", length = 50)
    private String modelo;

    @Column(name = "cor", length = 30)
    private String cor;

    @Column(name = "situacao")
    @Enumerated(EnumType.STRING)
    private StatusOs situacao;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private Status status;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "responsavel_id")
    private Usuario responsavel;

    @ManyToOne
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "orcamento_id", unique = true)
    private Orcamento orcamento;

    @Column(name = "data_fim")
    private LocalDate dataFim;

    @OneToMany(mappedBy = "os", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<HistoricoOs> historico;

    /** Construtor compatível com o modelo anterior à atribuição e encerramento explícitos. */
    public Os(Long id, LocalDate dataEmissao, String modelo, String cor, StatusOs situacao,
              Status status, Usuario usuario, Cliente cliente, Orcamento orcamento,
              List<HistoricoOs> historico) {
        this(id, dataEmissao, modelo, cor, situacao, status, usuario, null, cliente,
                orcamento, null, historico);
    }
}
