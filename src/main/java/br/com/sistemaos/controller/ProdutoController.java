package br.com.sistemaos.controller;

import br.com.sistemaos.applicationservice.ProdutoService;
import br.com.sistemaos.domain.entity.Produto;
import br.com.sistemaos.dto.ProdutoDTO;
import br.com.sistemaos.dto.SalvarProdutoDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import br.com.sistemaos.applicationservice.ProdutoService;

import java.net.URI;

@RestController
@RequestMapping("/produto" )
@RequiredArgsConstructor
public class ProdutoController {

    private final ProdutoService produtoService;


    @ResponseBody
    @RequestMapping(path = "/adicionar/", method = RequestMethod.POST)
    public ResponseEntity<ProdutoDTO> adicionarProduto(@RequestBody SalvarProdutoDTO salvarProdutoDTO) {
        Produto produto = produtoService.criarProduto(salvarProdutoDTO);
        return ResponseEntity.created(URI.create("/produto/" + produto.getId())).body(ProdutoDTO.criar(produto));
    }
}
