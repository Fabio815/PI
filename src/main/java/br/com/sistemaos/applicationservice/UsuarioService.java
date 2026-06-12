package br.com.sistemaos.applicationservice;

import br.com.sistemaos.domain.entity.Usuario;
import br.com.sistemaos.domain.model.Filtro;
import br.com.sistemaos.domain.model.Resposta;
import br.com.sistemaos.domain.model.Status;
import br.com.sistemaos.dto.ClienteRespostaDTO;
import br.com.sistemaos.dto.UsuarioDTO;
import br.com.sistemaos.dto.UsuariosRespostaDTO;
import br.com.sistemaos.repository.UsuarioCostumeizadoRepository;
import br.com.sistemaos.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class UsuarioService {
    private final UsuarioRepository usuarioRepository;
    private final UsuarioCostumeizadoRepository usuarioCostumeizadoRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioDTO cadastrar(UsuarioDTO usuarioDto) {
        UsuarioDTO resposta = new UsuarioDTO();

        if (usuarioDto == null) {
            log.info("O usuário não pode ser nulo");
            resposta.setResposta(Resposta.falha("O usuário não pode ser nulo"));
            return null;
        }
        usuarioDto.setSenha(passwordEncoder.encode(usuarioDto.getSenha()));

        Usuario usuario = Usuario.builder()
                .nome(usuarioDto.getNome())
                .email(usuarioDto.getEmail())
                .chave(usuarioDto.getChave())
                .senha(usuarioDto.getSenha())
                .build();


        usuarioRepository.save(usuario);

        resposta = usuarioDto;
        resposta.setResposta(Resposta.sucesso("Cliente cadstrado com sucesso!"));
        return resposta;
    }

    public Map<String, List<UsuarioDTO>> listar(String filtros) {
        ObjectMapper mapper = new ObjectMapper();
        List<Filtro> listaFiltros = new ArrayList<>();
        if (filtros != null) {
            listaFiltros = mapper.readValue(filtros, new TypeReference<List<Filtro>>() {});
        }
        List<Usuario> listUsuarios = usuarioCostumeizadoRepository.listagemUsuarios(listaFiltros);
        return UsuariosRespostaDTO.converterUsuarios(listUsuarios);
    }

    public Optional<UsuarioDTO> login(String email, String senha) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(email);

        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();

            if (passwordEncoder.matches(senha, usuario.getSenha())) {

                UsuarioDTO dto = new UsuarioDTO(
                        usuario.getId(),
                        usuario.getNome(),
                        usuario.getEmail(),
                        usuario.getChave(),
                        null,
                        new Resposta(true, "Login realizado com sucesso")
                );

                return Optional.of(dto);
            }
        }

        return Optional.empty();
    }

    public Resposta atualizarStats(UsuarioDTO usuario) {
        Resposta resposta = new Resposta();
        if (usuario.getId() == null) {
            resposta.setSucesso(false);
            resposta.setMensagem("Contate o administrador");
            return resposta;
        }
        Status status = usuario.getStatus().equals(Status.ATIVO) ? Status.INATIVO : Status.ATIVO;
        usuarioRepository.updateStatus(status, usuario.getId());
        resposta.setSucesso(true);
        resposta.setMensagem("Usuario atualizado com sucesso");
        return resposta;
    }

    public Resposta atualizarUsuario(UsuarioDTO usuario) {
        if (usuario.getId() == null) {
            return new Resposta(false, "Falha ao atualizar usuario");
        }
        Resposta resposta = new Resposta();
        usuarioRepository.updateUsuario(usuario.getEmail(), usuario.getNome(), usuario.getId());

        resposta.setSucesso(true);
        resposta.setMensagem("Usuario atualizado com sucesso");
        return resposta;
    }
}
