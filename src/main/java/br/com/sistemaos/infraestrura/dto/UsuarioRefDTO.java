package br.com.sistemaos.infraestrura.dto;

import br.com.sistemaos.domain.entity.Usuario;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UsuarioRefDTO {
    private Long id;
    private String nome;

    public static UsuarioRefDTO criar(Usuario usuario) {
        return usuario == null ? null : new UsuarioRefDTO(usuario.getId(), usuario.getNome());
    }
}
