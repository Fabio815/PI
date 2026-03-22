package br.com.sistemaos.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Endereco {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nome", length = 50)
    private String rua;

    @Column(name = "numero", length = 20)
    private String numero;

    @Column(name = "logradouro", length = 80)
    private String Logradouro;

    @Column(name = "complemento", length = 200)
    private String complemento;

    @OneToOne
    @JoinColumn(name = "id_cliente")
    private Cliente cliente;
}
