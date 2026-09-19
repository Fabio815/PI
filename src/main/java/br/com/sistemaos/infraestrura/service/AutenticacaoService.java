package br.com.sistemaos.infraestrura.service;

import br.com.sistemaos.domain.entity.Usuario;
import br.com.sistemaos.domain.exception.UsuarioNaoAutenticadoException;
import br.com.sistemaos.domain.model.Perfil;
import br.com.sistemaos.domain.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AutenticacaoService {
    private final UsuarioRepository usuarioRepository;

    public Usuario obterUsuarioAutenticado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UsuarioNaoAutenticadoException();
        }

        String email = authentication.getName();
        return usuarioRepository.findByEmail(email)
                .orElseThrow(UsuarioNaoAutenticadoException::new);
    }

    public Long obterIdUsuarioAutenticado() {
        return obterUsuarioAutenticado().getId();
    }

    public boolean ehAdministrador() {
        Usuario usuario = obterUsuarioAutenticado();
        return usuario.getChave() == Perfil.ADM;
    }

    public boolean podeEditarOs(Usuario criadoPor) {
        try {
            Usuario usuarioAutenticado = obterUsuarioAutenticado();
            return usuarioAutenticado.getId().equals(criadoPor.getId()) || ehAdministrador();
        } catch (UsuarioNaoAutenticadoException e) {
            return false;
        }
    }
}