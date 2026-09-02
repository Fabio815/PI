package br.com.sistemaos.infraestrura.dto;

import br.com.sistemaos.domain.entity.Usuario;
import br.com.sistemaos.domain.model.Perfil;
import br.com.sistemaos.domain.model.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class UsuarioDTO {
    private final Long id;
    private final String nome;
    private final String email;
    private final String senha;
    private final Perfil chave;
    private final Status status;

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
