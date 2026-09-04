package br.com.sistemaos.infraestrura.controller;

import br.com.sistemaos.domain.applicationservice.PecaService;
import br.com.sistemaos.domain.entity.Peca;
import br.com.sistemaos.domain.model.Status;
import br.com.sistemaos.infraestrura.dto.PecaDTO;
import br.com.sistemaos.infraestrura.dto.SalvarPecaDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/peca" )
@RequiredArgsConstructor
public class PecaController {

    private final PecaService pecaService;

    @PostMapping("/adicionar")
    public ResponseEntity<PecaDTO> adicionarPeca(@RequestBody @Valid SalvarPecaDTO salvarPecaDTO) {
        Peca peca = pecaService.adicionarPeca(salvarPecaDTO);
        return ResponseEntity.created(URI.create("/peca/" + peca.getId())).body(PecaDTO.criar(peca));
    }

    @GetMapping("/listar")
    public ResponseEntity<Map<String, Object>> listarPeca(
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) List<Status> status,
            @RequestParam(defaultValue = "0") int start,
            @RequestParam(defaultValue = "25") int limit) {
        int page = start / limit;
        Pageable pageable = PageRequest.of(page, limit);
        Map<String, Object> produtos = pecaService.listarPecas(nome, status, pageable);
        return ResponseEntity.ok(produtos);
    }

    @PutMapping("/atualizar/{id}")
    public ResponseEntity<PecaDTO> atualizarPeca(
        @PathVariable("id") Long id,
        @RequestBody @Valid SalvarPecaDTO salvarPecaDTO
    ) {
        Peca peca = pecaService.atualizarPeca(id, salvarPecaDTO);
        return ResponseEntity.ok(PecaDTO.criar(peca));
    }

    @PutMapping("/status/{id}")
    public ResponseEntity<PecaDTO> atualizarStatus(
            @PathVariable("id") Long id) {
        Peca peca = pecaService.atualizarStatus(id);
        return ResponseEntity.ok(PecaDTO.criar(peca));
    }

    @GetMapping("/listar/os")
    public ResponseEntity<Map<String, Object>> listarPecaOs(
            @RequestParam(required = false) String descricao,
            @RequestParam(defaultValue = "0") int start,
            @RequestParam(defaultValue = "25") int limit
            ) {
        int page = start / limit;
        Pageable pageable = PageRequest.of(page, limit);
        return ResponseEntity.ok(pecaService.listarPecasOs(descricao, pageable));
    }
}