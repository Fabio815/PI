package br.com.sistemaos.domain.entity;

import br.com.sistemaos.domain.model.TipoEventoHistorico;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "historico_os")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class HistoricoOs {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "data_alteracao", nullable = false)
    private LocalDateTime dataAlteracao;

    @Column(name = "campo_alterado", nullable = false, length = 50)
    private String campoAlterado;

    @Column(name = "de", length = 4000)
    private String de;

    @Column(name = "para", length = 4000)
    private String para;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_evento", length = 40)
    private TipoEventoHistorico tipoEvento;

    @Column(name = "descricao", length = 4000)
    private String descricao;

    @ManyToOne
    @JoinColumn(name = "responsavel_anterior_id")
    private Usuario responsavelAnterior;

    @ManyToOne
    @JoinColumn(name = "novo_responsavel_id")
    private Usuario novoResponsavel;

    @ManyToOne
    @JoinColumn(name = "os_id", nullable = false)
    private Os os;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    /** Construtor legado: eventos existentes não recebem tipo inventado. */
    public HistoricoOs(Long id, LocalDateTime dataAlteracao, String campoAlterado, String de,
                       String para, Os os, Usuario usuario) {
        this(id, dataAlteracao, campoAlterado, de, para, null, null, null, null, os, usuario);
    }
}
