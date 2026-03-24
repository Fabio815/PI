package br.com.sistemaos.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;


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

    @JsonIgnore
    @ToString.Exclude
    @OneToOne
    @JoinColumn(name = "id_cliente")
    private Cliente cliente;
}
