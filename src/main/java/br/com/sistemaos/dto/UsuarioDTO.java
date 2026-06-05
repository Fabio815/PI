package br.com.sistemaos.dto;

import br.com.sistemaos.domain.model.Resposta;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class UsuarioDTO {
    private Long id;
    private String nome;
    private String email;
    private String senha;
    private String chave;
    private Resposta resposta;
}
