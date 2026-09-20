package br.com.sistemaos.infraestrura.security;

import br.com.sistemaos.domain.entity.Usuario;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class UsuarioLogadoService {

    /**
     * @return o usuário autenticado na sessão atual, ou null se não houver login (ex: rota pública).
     */
    public Usuario obterUsuarioLogado() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof UsuarioPrincipal principal)) {
            return null;
        }
        return principal.getUsuario();
    }
}
