package br.com.sistemaos.controller;

import br.com.sistemaos.applicationservice.UsuarioService;
import br.com.sistemaos.domain.entity.Usuario;
import br.com.sistemaos.domain.model.Filtro;
import br.com.sistemaos.domain.model.Resposta;
import br.com.sistemaos.dto.FiltroDTO;
import br.com.sistemaos.dto.UsuarioDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/usuarios" )
@RequiredArgsConstructor
public class UsuarioController {
    private final UsuarioService usuarioService;

    @PostMapping("/cadastro")
    public ResponseEntity<UsuarioDTO> cadastrar(@RequestBody UsuarioDTO usuario) {
        return ResponseEntity.ok(usuarioService.cadastrar(usuario));
    }

    @GetMapping("/listar")
    public ResponseEntity<Map<String, Object>> listar(
            @RequestParam(value = "start", defaultValue = "0") int start,
            @RequestParam(value = "limit", defaultValue = "10") int limit,
            @RequestParam(value = "filtros", required = false) String filtros) {

        return ResponseEntity.ok(
                usuarioService.listar(filtros, start, limit)
        );
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials) {

        String email = credentials.get("email");
        String senha = credentials.get("senha");

        Optional<UsuarioDTO> usuario = usuarioService.login(email, senha);

        if (usuario.isPresent()) {
            return ResponseEntity.ok(usuario.get());
        }
        Map<String, Resposta> resposta = new HashMap<>();
        resposta.put("resposta", new Resposta(false, "E-mail ou senha inválidos"));
        return ResponseEntity
                .ok()
                .body(resposta);
    }

    @PutMapping("/atualizar")
    public ResponseEntity<Resposta> atualizarUsuario(@RequestBody UsuarioDTO usuario) {
        return ResponseEntity.ok(usuarioService.atualizarUsuario(usuario));
    }

    @PostMapping("/status/atualizar")
    public ResponseEntity<Resposta> atualizarStatus(@RequestBody UsuarioDTO usuario) {
        return ResponseEntity.ok(usuarioService.atualizarStats(usuario));
    }
}