package br.com.sistemaos.dto;

import br.com.sistemaos.domain.entity.Usuario;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UsuariosRespostaDTO {

    public static Map<String, List<UsuarioDTO>> converterUsuarios(List<Usuario> usuario) {
        if (usuario == null) {
            return null;
        }
        List<UsuarioDTO> listUsuarioDTO = new ArrayList<>();
        Map<String, List<UsuarioDTO>> mapaUsuarios = new HashMap<String, List<UsuarioDTO>>();
        for (Usuario u : usuario) {
            listUsuarioDTO.add(new UsuarioDTO(u.getId(), u.getNome(), u.getEmail(), u.getSenha(), u.getChave()));
        }

        mapaUsuarios.put("usuarios", listUsuarioDTO);
        return mapaUsuarios;
    }
}
