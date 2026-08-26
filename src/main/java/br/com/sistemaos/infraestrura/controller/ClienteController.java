package br.com.sistemaos.infraestrura.controller;

import br.com.sistemaos.domain.applicationservice.ClienteService;
import br.com.sistemaos.domain.entity.Cliente;
import br.com.sistemaos.domain.model.Resposta;
import br.com.sistemaos.infraestrura.dto.ClienteDTO;
import br.com.sistemaos.infraestrura.dto.ClienteRespostaDTO;
import br.com.sistemaos.infraestrura.dto.SalvarClienteDTO;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
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
    public ResponseEntity<List<ClienteDTO>> listar(
            @RequestParam(value = "id", required = false) Long id,
            @RequestParam(value = "nome", required = false) String nome,
            @RequestParam(value = "status", required = false) List<String> status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "25") int size)
    {
        Pageable pageable = PageRequest.of(page, size);
        List<Cliente> clientes = clienteService.listarClientes(id, nome, status, pageable);
        return ResponseEntity.ok(clientes.stream().map(c -> ClienteDTO.criar(c)).toList());
    }


    @RequestMapping(path = "/atualizar/{id}", method = RequestMethod.PUT)
    public ResponseEntity<ClienteRespostaDTO> atualizar(@RequestBody ClienteDTO cliente, @PathVariable Long id) {
        return ResponseEntity.ok().body(clienteService.atualizarClienteId(cliente, id));
    }

    /*@RequestMapping(path = "/status/{id}/{status}", method = RequestMethod.PUT)
    public ResponseEntity<Resposta> atualizarStatus(@PathVariable Long id, @PathVariable String status) {
        return ResponseEntity.ok().body(clienteService.atualizarStatus(status, id));
    }*/
    @RequestMapping(path = "/status", method = RequestMethod.POST)
    public ResponseEntity<Resposta> atualizarStatus(@RequestBody ClienteDTO cliente) {
        return ResponseEntity.ok().body(clienteService.atualizarStatus(cliente));
    }



    @GetMapping("/teste")
    public ResponseEntity<String> teste() {
        return ResponseEntity.ok("Ok");
    }
}