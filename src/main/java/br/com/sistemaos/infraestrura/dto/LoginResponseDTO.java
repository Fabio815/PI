package br.com.sistemaos.infraestrura.dto;

import br.com.sistemaos.domain.entity.Usuario;
import br.com.sistemaos.domain.model.Perfil;
import lombok.Data;

@Data
public class LoginResponseDTO {
    private final Long id;
    private final String nome;
    private final Perfil chave;
    private final Resposta resposta;

    public static LoginResponseDTO sucesso(Usuario usuario) {
        return new LoginResponseDTO(
                usuario.getId(),
                usuario.getNome(),
                usuario.getChave(),
                new Resposta(true, "Login realizado com sucesso")
        );
    }

    public static LoginResponseDTO erro(String mensagem) {
        return new LoginResponseDTO(null, null, null, new Resposta(false, mensagem));
    }
}
