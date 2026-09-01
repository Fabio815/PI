package br.com.sistemaos.domain.entity;

import br.com.sistemaos.domain.model.Perfil;
import br.com.sistemaos.domain.model.Status;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "usuarios")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "nome", nullable = false, length = 50)
    private String nome;

    @Column(name = "email", nullable = false, length = 80)
    private String email;

    @Column(name = "senha", nullable = false)
    private String senha;

    @Column(name = "chave", nullable = false)
    @Enumerated(EnumType.STRING)
    private Perfil chave;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private Status status;

    //recuperacao de senha
    @Column(name = "reset_token")
    private String resetToken;

    //relacionamento
    @Column(name = "reset_token_expiry_date")
    private LocalDateTime resetTokenExpiryDate;

    @OneToMany(mappedBy = "usuario")
    private List<Os> ordensServico;
}
