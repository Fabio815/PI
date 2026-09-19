package br.com.sistemaos.infraestrura.controller;

import br.com.sistemaos.domain.applicationservice.UsuarioService;
import br.com.sistemaos.infraestrura.dto.LoginDTO;
import br.com.sistemaos.infraestrura.dto.UsuarioDTO;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@AllArgsConstructor
public class AuthController {
    private final UsuarioService usuarioService;
    private final AuthenticationManager authenticationManager;

    @PostMapping("/login")
    public ResponseEntity<UsuarioDTO> login(@RequestBody LoginDTO loginDTO) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDTO.getEmail(), loginDTO.getSenha())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        var usuario = usuarioService.carregarPorEmail(loginDTO.getEmail());
        return ResponseEntity.ok(UsuarioDTO.criar(usuario));
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout() {
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok("Logout realizado com sucesso!");
    }

    @PostMapping("/recuperar")
    public ResponseEntity<String> recuperar(@RequestBody Map<String, String> email) {
        usuarioService.solicitarRecuperacao(email.get("email"));
        return ResponseEntity.ok("E-mail de recuperação enviado!");
    }

    /*@PostMapping("/recuperar")
    public ResponseEntity<String> recuperar(@RequestParam String email) {
        usuarioService.solicitarRecuperacao(email);
        return ResponseEntity.ok("E-mail de recuperação enviado!");
    }*/

    /*@PostMapping("/resetar-senha")
    public ResponseEntity<String> resetar(@RequestParam String token, @RequestParam String novaSenha) {
        if (usuarioService.resetarSenha(token, novaSenha)) {
            return ResponseEntity.ok("Senha alterada!");
        }
        return ResponseEntity.badRequest().body("Token inválido ou expirado.");
    }
    @GetMapping("/resetar-senha")
    public ResponseEntity<String> validarTokenETrocarSenha(
            @RequestParam String token,
            @RequestParam(required = false, defaultValue = "novasenha") String novaSenha) {

        // Como o navegador não tem um formulário para digitar a senha ainda, senha adicionada pelo back
        if (usuarioService.resetarSenha(token, novaSenha)) {
            return ResponseEntity.ok("Sucesso! Senha alterada no banco para: " + novaSenha);
        }
        return ResponseEntity.badRequest().body("Token inválido ou expirado.");
    }*/
    @PostMapping("/resetar-senha")
    public ResponseEntity<String> resetar(@RequestBody Map<String, String> payload) {
        String token = payload.get("token");
        String novaSenha = payload.get("novaSenha");

        if (usuarioService.resetarSenha(token, novaSenha)) {
            return ResponseEntity.ok("Senha alterada com sucesso!");
        }
        return ResponseEntity.badRequest().body("Token inválido ou expirado.");
    }
}