package br.com.sistemaos.dto;

import br.com.sistemaos.domain.model.Resposta;
import br.com.sistemaos.domain.model.Status;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class UsuarioDTO {
    private Long id;
    private String nome;
    private String email;
    private String senha;
    private String chave;
    private Status status;
    private Resposta resposta;

    public UsuarioDTO(Long id, String nome, String email, String chave, Status status) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.chave = chave;
        this.status = status;
    }

    public UsuarioDTO(Long id, String nome, String email, String chave, Status o, Resposta loginRealizadoComSucesso) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.chave = chave;
        this.resposta = loginRealizadoComSucesso;
    }
}
