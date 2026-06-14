package br.com.sistemaos.controller;

import br.com.sistemaos.applicationservice.UsuarioService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth" )
public class AuthController {
    private final UsuarioService usuarioService;

    public AuthController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
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