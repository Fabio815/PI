package br.com.sistemaos.infraestrura.controller;

import br.com.sistemaos.domain.applicationservice.ProdutoService;
import br.com.sistemaos.domain.entity.Produto;
import br.com.sistemaos.domain.model.Resposta;
import br.com.sistemaos.domain.model.Status;
import br.com.sistemaos.infraestrura.dto.ProdutoDTO;
import br.com.sistemaos.infraestrura.dto.SalvarProdutoDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/produto" )
@RequiredArgsConstructor
public class ProdutoController {

    private final ProdutoService produtoService;

    @PostMapping("/adicionar")
    public ResponseEntity<ProdutoDTO> adicionarProduto(@RequestBody SalvarProdutoDTO salvarProdutoDTO) {
        Produto produto = produtoService.criarProduto(salvarProdutoDTO);
        return ResponseEntity.created(URI.create("/produto/" + produto.getId())).body(ProdutoDTO.criar(produto));
    }

    @GetMapping("/carregar/{id}")
    public ResponseEntity<ProdutoDTO> carregarProduto(@PathVariable("id") Long id) {
        Produto produto = produtoService.carregarProdutoPorId(id);
        return ResponseEntity.ok(ProdutoDTO.criar(produto));
    }

    @GetMapping("/listar")
    public ResponseEntity<Page<ProdutoDTO>> listarProdutos(
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) List<Status> status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "25") int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Produto> produtos = produtoService.listarProdutos(nome, status, pageable);
        Page<ProdutoDTO> produtosDTO = produtos.map(ProdutoDTO::criar);
        return ResponseEntity.ok(produtosDTO);
    }

    @PutMapping("/atualizar/{id}")
    public ResponseEntity<ProdutoDTO> atualizarProduto(
        @PathVariable("id") Long id,
        @RequestBody @Valid SalvarProdutoDTO salvarProdutoDTO
    ) {
        Produto produto = produtoService.atualizarProduto(id, salvarProdutoDTO);
        return ResponseEntity.ok(ProdutoDTO.criar(produto));
    }

    @RequestMapping(path = "/status", method = RequestMethod.POST)
    public ResponseEntity<Resposta> atualizarStatus(@RequestBody ProdutoDTO produto) {
        return ResponseEntity.ok().body(produtoService.atualizarStatus(produto));
    }
}