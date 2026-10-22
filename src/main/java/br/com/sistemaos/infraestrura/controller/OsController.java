package br.com.sistemaos.infraestrura.controller;

import br.com.sistemaos.domain.applicationservice.OsService;
import br.com.sistemaos.domain.entity.Os;
import br.com.sistemaos.domain.model.Status;
import br.com.sistemaos.infraestrura.dto.*;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/os")
@AllArgsConstructor
public class OsController {
    private final OsService osService;

    @PostMapping("/cadastrar")
    public ResponseEntity<Map<String, Object>> cadastrar(@RequestBody @Valid SalvarOsDTO dto) {
        OsService.ResultadoCadastroOs resultado = osService.adicionarOs(dto);
        Map<String, Object> resposta = new LinkedHashMap<>();
        resposta.put("os", OsDTO.criar(resultado.os()));
        resposta.put("avisosEstoque", resultado.avisosEstoque());
        return ResponseEntity.created(URI.create("/os/" + resultado.os().getId())).body(resposta);
    }

    @GetMapping("/responsaveis")
    public ResponseEntity<List<UsuarioRefDTO>> responsaveis(
            @RequestParam(defaultValue = "false") boolean incluirInativos) {
        return ResponseEntity.ok(osService.listarResponsaveis(incluirInativos));
    }

    @GetMapping("/listar")
    public ResponseEntity<Map<String, Object>> listar(
            @RequestParam(value = "id", required = false) Long id,
            @RequestParam(value = "status", required = false) List<Status> status,
            @RequestParam(value = "nome", required = false) String nome,
            @RequestParam(defaultValue = "0") int start,
            @RequestParam(defaultValue = "25") int limit) {
        if (start < 0 || limit < 1) throw new br.com.sistemaos.infraestrura.exception.RequestException("Paginação inválida", "Erro de validação");
        Pageable pageable = PageRequest.of(start / limit, limit);
        return ResponseEntity.ok(osService.listarOs(id, nome, status, pageable));
    }

    @PutMapping("/atualizar/{id}")
    public ResponseEntity<OsDTO> atualizar(@PathVariable Long id, @RequestBody @Valid SalvarOsDTO dto) {
        return ResponseEntity.ok(OsDTO.criar(osService.atualizarOs(id, dto)));
    }

    @PutMapping("/{id}/responsavel")
    public ResponseEntity<OsDTO> atribuirResponsavel(@PathVariable Long id,
                                                       @RequestBody @Valid AtribuirResponsavelDTO dto) {
        return ResponseEntity.ok(OsDTO.criar(osService.atribuirResponsavel(id, dto.getResponsavelId())));
    }

    @PutMapping("/{id}/situacao")
    public ResponseEntity<OsDTO> atualizarSituacao(@PathVariable Long id,
                                                     @RequestBody @Valid AtualizarSituacaoOsDTO dto) {
        return ResponseEntity.ok(OsDTO.criar(osService.atualizarSituacao(id, dto.getSituacao())));
    }

    @PostMapping("/{id}/reabrir")
    public ResponseEntity<OsDTO> reabrir(@PathVariable Long id) {
        return ResponseEntity.ok(OsDTO.criar(osService.reabrir(id)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OsDTO> carregar(@PathVariable Long id) {
        return ResponseEntity.ok(OsDTO.criar(osService.carregarOs(id)));
    }

    /** Rota antiga de alternância ativo/inativo; não altera a situação do serviço. */
    @PutMapping("/status/{id}")
    public ResponseEntity<OsDTO> atualizarStatus(@PathVariable Long id) {
        return ResponseEntity.ok(OsDTO.criar(osService.atualizarStatus(id)));
    }

    @GetMapping("/{id}/historico")
    public ResponseEntity<List<HistoricoOsDTO>> listarHistorico(
            @PathVariable Long id,
            @RequestParam(value = "tipoEvento", required = false) String tipoEvento,
            @RequestParam(value = "dataInicio", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam(value = "dataFim", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim,
            @RequestParam(value = "usuarioId", required = false) Long usuarioId) {
        return ResponseEntity.ok(osService.listarHistorico(id, tipoEvento, dataInicio, dataFim, usuarioId));
    }

    @GetMapping("/{id}/comentarios")
    public ResponseEntity<List<ComentarioOsDTO>> listarComentarios(@PathVariable Long id) {
        return ResponseEntity.ok(osService.listarComentarios(id));
    }

    @PostMapping("/{id}/comentarios")
    public ResponseEntity<ComentarioOsDTO> criarComentario(@PathVariable Long id,
                                                              @RequestBody @Valid SalvarComentarioDTO dto) {
        ComentarioOsDTO criado = osService.criarComentario(id, dto);
        return ResponseEntity.created(URI.create("/os/" + id + "/comentarios/" + criado.getId())).body(criado);
    }

    @PutMapping("/{id}/comentarios/{comentarioId}")
    public ResponseEntity<ComentarioOsDTO> editarComentario(@PathVariable Long id,
            @PathVariable Long comentarioId, @RequestBody @Valid SalvarComentarioDTO dto) {
        return ResponseEntity.ok(osService.editarComentario(id, comentarioId, dto));
    }
}
