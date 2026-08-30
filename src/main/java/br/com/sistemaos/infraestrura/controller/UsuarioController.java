package br.com.sistemaos.infraestrura.controller;

import br.com.sistemaos.domain.applicationservice.UsuarioService;
import br.com.sistemaos.domain.entity.Usuario;
import br.com.sistemaos.infraestrura.dto.AtualizarUsuarioDTO;
import br.com.sistemaos.infraestrura.dto.SalvarUsuarioDTO;
import br.com.sistemaos.infraestrura.dto.UsuarioDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/usuarios" )
@RequiredArgsConstructor
public class UsuarioController {
    private final UsuarioService usuarioService;

    @PostMapping("/adicionar")
    public ResponseEntity<UsuarioDTO> adicionar(@RequestBody @Valid SalvarUsuarioDTO salvarUsuarioDTO) {
        Usuario usuario = usuarioService.adicionarUsuario(salvarUsuarioDTO);
        return ResponseEntity.created(URI.create("/usuario/" + usuario.getId())).body(UsuarioDTO.criar(usuario));
    }

    @GetMapping("/listar")
    public ResponseEntity<Map<String, Object>> listar(
            @RequestParam(value = "start", defaultValue = "0") int start,
            @RequestParam(value = "limit", defaultValue = "10") int limit,
            @RequestParam(value = "id", required = false) Long id,
            @RequestParam(value = "nome", required = false) String nome,
            @RequestParam(value = "status", required = false) List<String> status,
            @RequestParam(value = "email", required = false) String email) {
        int page = start / limit;
        Pageable pageable = PageRequest.of(page, limit);
        Map<String, Object> usuarios = usuarioService.listarUsuarios(id, nome, status, email, pageable);
        return ResponseEntity.ok(usuarios);
    }

    /*@PostMapping("/login")
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
    }*/

    @PutMapping("/atualizar/{id}")
    public ResponseEntity<UsuarioDTO> atualizarUsuario(
            @PathVariable("id") Long id, @RequestBody @Valid AtualizarUsuarioDTO atualizarUsuarioDTO) {
        Usuario usuario = usuarioService.atualizarUsuario(id, atualizarUsuarioDTO);
        return ResponseEntity.ok(UsuarioDTO.criar(usuario));
    }

    @PutMapping("/atualizar/status/{id}")
    public ResponseEntity<UsuarioDTO> atualizarStatus(
            @PathVariable("id") Long id) {
        Usuario usuario = usuarioService.atualizarStatus(id);
        return ResponseEntity.ok(UsuarioDTO.criar(usuario));
    }
}