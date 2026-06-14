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
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Optional;
import java.time.LocalDateTime;
import java.util.UUID;
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
    private final EmailService emailService;

    @Value("${app.base-url:http://localhost:8080}")
    private String baseUrl; // Pega a URL do application.yml mas não esta reconhecendo

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
    //Recuperacao de senha

    public void solicitarRecuperacao(String email) {
        usuarioRepository.findByEmail(email).ifPresent(usuario -> {
            // Gera um token único e define expiração para 1 hora
            String token = UUID.randomUUID().toString();
            usuario.setResetToken(token);
            usuario.setResetTokenExpiryDate(LocalDateTime.now().plusHours(1));
            usuarioRepository.save(usuario);

            // Monta o link e envia o e-mail
            //String link = baseUrl + "/auth/resetar-senha?token=" + token;
            //String link = baseUrl + "/auth/resetar-senha?token=" + token;
            String link = baseUrl + "/#redefinir-senha?token=" + token;
            emailService.sendEmail(
                    usuario.getEmail(),
                    "Recuperação de Senha - Sistema OS",
                    "Olá " + usuario.getNome() + ",\n\nPara redefinir sua senha, clique no link abaixo:\n" + link
            );
            log.info(link);
        });
    }

    public boolean resetarSenha(String token, String novaSenha) {
        return usuarioRepository.findByResetToken(token)
                .filter(usuario -> usuario.getResetTokenExpiryDate().isAfter(LocalDateTime.now()))
                .map(usuario -> {
                    // Criptografa a nova senha e limpa o token
                    usuario.setSenha(passwordEncoder.encode(novaSenha));
                    usuario.setResetToken(null);
                    usuario.setResetTokenExpiryDate(null);
                    usuarioRepository.save(usuario);
                    return true;
                }).orElse(false);
    }
}