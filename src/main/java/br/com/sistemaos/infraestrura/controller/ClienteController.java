package br.com.sistemaos.infraestrura.controller;

import br.com.sistemaos.domain.applicationservice.ClienteService;
import br.com.sistemaos.domain.entity.Cliente;
import br.com.sistemaos.infraestrura.dto.ClienteDTO;
import br.com.sistemaos.infraestrura.dto.SalvarClienteDTO;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Map;

//Ultima modificação 23/03/26
@RestController
@RequestMapping("/cliente")//Teste do banco de dados
@AllArgsConstructor
@Slf4j
public class ClienteController {
    private final ClienteService clienteService;
    /*public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }*/

    @PostMapping("/cadastrar")
    public ResponseEntity<ClienteDTO> cadastrar(@RequestBody @Valid SalvarClienteDTO clienteDTO) {
        Cliente cliente = clienteService.adicionarCliente(clienteDTO);
        return ResponseEntity.created(URI.create("/projeto/" + cliente.getId())).body(ClienteDTO.criar(cliente));
    }

    //@GetMapping
    @GetMapping("/listar")
    public ResponseEntity<Map<String, Object>> listar(
            @RequestParam(value = "id", required = false) Long id,
            @RequestParam(value = "nome", required = false) String nome,
            @RequestParam(value = "status", required = false) List<String> status,
            @RequestParam(defaultValue = "0") int start,
            @RequestParam(defaultValue = "25") int limit)
    {
        int page = start / limit;
        Pageable pageable = PageRequest.of(page, limit);
        Map<String, Object> clientes = clienteService.listarClientes(id, nome, status, pageable);
        return ResponseEntity.ok(clientes);
    }

    @PutMapping("/atualizar/{id}")
    public ResponseEntity<ClienteDTO> atualizar(
            @PathVariable("id") Long id, @RequestBody @Valid SalvarClienteDTO salvarClienteDTO) {
        Cliente cliente = clienteService.atualizarCliente(id, salvarClienteDTO);
        return ResponseEntity.ok(ClienteDTO.criar(cliente));
    }

    @PutMapping("/status/{id}")
    public ResponseEntity<ClienteDTO> deletar(
            @PathVariable("id") Long id) {
        Cliente cliente = clienteService.atualizarStatus(id);
        return ResponseEntity.ok(ClienteDTO.criar(cliente));
    }

    @GetMapping("/listar/os")
    public ResponseEntity<Map<String, Object>> listarClienteOs(
            @RequestParam(value = "telefone", required = false) String telefone,
            @RequestParam(defaultValue = "0") int start,
            @RequestParam(defaultValue = "25") int limit)
    {
        int page = start / limit;
        Pageable pageable = PageRequest.of(page, limit);
        Map<String, Object> clientes = clienteService.listarClienteOs(telefone, pageable);
        return ResponseEntity.ok(clientes);
    }

    @GetMapping("/teste")
    public ResponseEntity<String> teste() {
        return ResponseEntity.ok("Ok");
    }
}