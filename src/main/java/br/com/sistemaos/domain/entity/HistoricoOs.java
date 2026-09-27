package br.com.sistemaos.domain.entity;

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

    @Column(name = "de", length = 100)
    private String de;

    @Column(name = "para", length = 100)
    private String para;

    //relacionamento
    @ManyToOne
    @JoinColumn(name = "os_id", nullable = false)
    private Os os;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;
}