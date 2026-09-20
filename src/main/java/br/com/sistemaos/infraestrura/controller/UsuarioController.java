package br.com.sistemaos.infraestrura.controller;

import br.com.sistemaos.domain.applicationservice.UsuarioService;
import br.com.sistemaos.domain.entity.Usuario;
import br.com.sistemaos.infraestrura.dto.AtualizarUsuarioDTO;
import br.com.sistemaos.infraestrura.dto.LoginResponseDTO;
import br.com.sistemaos.infraestrura.dto.SalvarUsuarioDTO;
import br.com.sistemaos.infraestrura.dto.UsuarioDTO;
import br.com.sistemaos.infraestrura.security.UsuarioLogadoService;
import br.com.sistemaos.infraestrura.security.UsuarioPrincipal;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/usuarios" )
@RequiredArgsConstructor
public class UsuarioController {
    private final UsuarioService usuarioService;
    private final UsuarioLogadoService usuarioLogadoService;
    private final AuthenticationManager authenticationManager;
    private final SecurityContextRepository securityContextRepository;

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

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody Map<String, String> credentials,
                                                   HttpServletRequest request,
                                                   HttpServletResponse response) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(credentials.get("email"), credentials.get("senha"))
            );

            SecurityContext context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(authentication);
            SecurityContextHolder.setContext(context);
            // Persiste o contexto na sessão HTTP, para as próximas requisições já virem autenticadas
            securityContextRepository.saveContext(context, request, response);

            Usuario usuario = ((UsuarioPrincipal) authentication.getPrincipal()).getUsuario();
            return ResponseEntity.ok(LoginResponseDTO.sucesso(usuario));
        } catch (BadCredentialsException | org.springframework.security.core.userdetails.UsernameNotFoundException e) {
            return ResponseEntity.ok(LoginResponseDTO.erro("E-mail ou senha inválidos"));
        } catch (org.springframework.security.authentication.DisabledException e) {
            return ResponseEntity.ok(LoginResponseDTO.erro("Usuário inativo"));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request) {
        SecurityContextHolder.clearContext();
        var session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        return ResponseEntity.ok().build();
    }

    @GetMapping("/sessao")
    public ResponseEntity<UsuarioDTO> sessao() {
        Usuario usuario = usuarioLogadoService.obterUsuarioLogado();
        if (usuario == null) {
            return ResponseEntity.status(401).build();
        }
        return ResponseEntity.ok(UsuarioDTO.criar(usuario));
    }

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