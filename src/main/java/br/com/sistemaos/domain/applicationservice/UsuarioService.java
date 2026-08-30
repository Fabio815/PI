package br.com.sistemaos.domain.applicationservice;

import br.com.sistemaos.domain.entity.Usuario;
import br.com.sistemaos.domain.exception.ConverteStatusException;
import br.com.sistemaos.domain.exception.UsuarioNaoEncontradoException;
import br.com.sistemaos.domain.model.Status;
import br.com.sistemaos.domain.repository.UsuarioRepository;
import br.com.sistemaos.infraestrura.dto.SalvarUsuarioDTO;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class UsuarioService {
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    @Value("${app.base-url:http://localhost:8080}")
    private String baseUrl; // Pega a URL do application.yml mas não esta reconhecendo

    @Transactional
    public Usuario adicionarUsuario(SalvarUsuarioDTO salvarUsuarioDTO) {
        Usuario usuario = Usuario.builder()
                .nome(salvarUsuarioDTO.getNome())
                .email(salvarUsuarioDTO.getEmail())
                .senha(salvarUsuarioDTO.getSenha())
                .status(Status.ATIVO)
                .chave(salvarUsuarioDTO.getChave())
                .build();

        usuarioRepository.save(usuario);
        return usuario;
    }

    public List<Usuario> listarUsuarios(Long id, String nome, List<String> status, String email, Pageable pageable) {
        List<Usuario> listaUsuario = new ArrayList<>();
        if (!Objects.isNull(id) || !Objects.isNull(nome) || !Objects.isNull(status) || !Objects.isNull(email)) {
            Page<Usuario> usuarios = usuarioRepository.listarUsuarios(nome, email, converterParaStatusList(status), id, pageable);
            if (!usuarios.isEmpty()) {
                for (Usuario us : usuarios) {
                    listaUsuario.add(us);
                }
            }
        } else {
            Page<Usuario> usuarios = usuarioRepository.findAll(pageable);
            if (!usuarios.isEmpty()) {
                for (Usuario us : usuarios) {
                    listaUsuario.add(us);
                }
            }
        }
        return listaUsuario;
    }

    /*
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
    }*/

    @Transactional
    public Usuario atualizarUsuario(Long id, SalvarUsuarioDTO salvarUsuarioDTO) {
        Usuario usuario = carregarUsuario(id);

        usuario.setNome(salvarUsuarioDTO.getNome());
        usuario.setEmail(salvarUsuarioDTO.getEmail());
        usuario.setSenha(salvarUsuarioDTO.getSenha());
        usuario.setChave(salvarUsuarioDTO.getChave());

        return usuario;
    }

    @Transactional
    public Usuario atualizarStatus(Long id) {
        Usuario usuario = carregarUsuario(id);
        usuario.setStatus(trocarStatus(usuario));
        return usuario;
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

    private Usuario carregarUsuario(Long id) {
        Optional<Usuario> op = usuarioRepository.findById(id);
        if (op.isEmpty()) {
            throw new UsuarioNaoEncontradoException(id);
        }
        return op.get();
    }

    private List<Status> converterParaStatusList(List<String> status) {
        if (status == null) {
            return null;
        }
        try {
            return status.stream().map(s -> Status.valueOf(s)).toList();
        } catch (IllegalArgumentException | NullPointerException e) {
            throw new ConverteStatusException(status.toString());
        }
    }

    private Status trocarStatus(Usuario usuario) {
        if (usuario.getStatus() == Status.ATIVO) {
            return Status.INATIVO;
        } else {
            return  Status.ATIVO;
        }
    }
}