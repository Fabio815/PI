package br.com.sistemaos.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


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

    //relacionamento
    @OneToOne(mappedBy = "endereco")
    private Cliente cliente;
}
