package br.com.sistemaos.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "endereco")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class Endereco {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "rua", length = 50)
    private String rua;

    @Column(name = "numero", length = 20)
    private String numero;

    @Column(name = "logradouro", length = 80)
    private String logradouro;

    @Column(name = "complemento", length = 200)
    private String complemento;

    @OneToOne
    @JoinColumn(name = "id_cliente")
    private Cliente cliente;
}
