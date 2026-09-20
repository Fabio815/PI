package br.com.sistemaos.infraestrura.security;

import br.com.sistemaos.domain.entity.Usuario;
import br.com.sistemaos.domain.model.Status;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/**
 * Adapta a entidade Usuario para o contrato que o Spring Security espera,
 * mantendo o Usuario acessível para o restante da aplicação (id, perfil, etc.).
 */
@Getter
public class UsuarioPrincipal implements UserDetails {

    private final Usuario usuario;

    public UsuarioPrincipal(Usuario usuario) {
        this.usuario = usuario;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + usuario.getChave().name()));
    }

    @Override
    public String getPassword() {
        return usuario.getSenha();
    }

    @Override
    public String getUsername() {
        return usuario.getEmail();
    }

    @Override
    public boolean isEnabled() {
        return usuario.getStatus() == Status.ATIVO;
    }
}
