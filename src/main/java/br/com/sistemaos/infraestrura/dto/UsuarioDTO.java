package br.com.sistemaos.infraestrura.dto;

import br.com.sistemaos.domain.entity.Usuario;
import br.com.sistemaos.domain.model.Perfil;
import br.com.sistemaos.domain.model.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UsuarioDTO {
    private Long id;
    private String nome;
    private String email;
    private String senha;
    private Perfil chave;
    private Status status;

    public static UsuarioDTO criar(Usuario usuario) {
        return new UsuarioDTO(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                null,
                usuario.getChave(),
                usuario.getStatus()
        );
    }
}
