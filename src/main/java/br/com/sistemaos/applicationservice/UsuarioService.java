package br.com.sistemaos.applicationservice;

import br.com.sistemaos.domain.entity.Usuario;
import br.com.sistemaos.domain.model.Resposta;
import br.com.sistemaos.dto.UsuarioDTO;
import br.com.sistemaos.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Optional;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UsuarioService {
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    @Value("${app.base-url:http://localhost:8080}")
    private String baseUrl; // Pega a URL do application.yml mas não esta reconhecendo

    public Usuario cadastrar(Usuario usuario) {
        usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
        return usuarioRepository.save(usuario);
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
                        null,
                        new Resposta(true, "Login realizado com sucesso")
                );

                return Optional.of(dto);
            }
        }

        return Optional.empty();
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
            String link = baseUrl + "/auth/resetar-senha?token=" + token;
            emailService.sendEmail(
                    usuario.getEmail(),
                    "Recuperação de Senha - Sistema OS",
                    "Olá " + usuario.getNome() + ",\n\nPara redefinir sua senha, clique no link abaixo:\n" + link
            );
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