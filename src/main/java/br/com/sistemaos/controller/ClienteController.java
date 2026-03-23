package br.com.sistemaos.controller;

import br.com.sistemaos.applicationservice.ClienteService;
import br.com.sistemaos.domain.entity.Cliente;
import br.com.sistemaos.dto.ClienteDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

//Ultima modificação 23/03/26
@RestController
@RequestMapping("/cliente") //Teste do banco de dados
public class ClienteController {

    private final ClienteService clienteService; //Para não ter alteração de dados "final"

    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    @PostMapping
    //Cadastro do cliente
    public ResponseEntity<Cliente> cadastrar(@RequestBody Cliente cliente) { //Mapeia o corpo
        Cliente salvo = clienteService.adicionarCliente(cliente);
        return ResponseEntity.ok(salvo);
    }

    @GetMapping("/teste")
    public ResponseEntity<String> teste() {
        return ResponseEntity.ok("Ok");
    }
}