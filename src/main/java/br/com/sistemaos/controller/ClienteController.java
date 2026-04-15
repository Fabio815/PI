package br.com.sistemaos.controller;

import br.com.sistemaos.applicationservice.ClienteService;
import br.com.sistemaos.domain.entity.Cliente;
import br.com.sistemaos.dto.ClienteDTO;
import br.com.sistemaos.dto.ClienteRespostaDTO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

//Ultima modificação 23/03/26
@RestController
@RequestMapping("/cliente") //Teste do banco de dados
@Slf4j
public class ClienteController {

    private final ClienteService clienteService; //Para não ter alteração de dados "final"

    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    //@PostMapping
    @ResponseBody
    @RequestMapping(path = "/cadastrar", method = RequestMethod.POST)
    //Cadastro do cliente
    public ResponseEntity<ClienteRespostaDTO> cadastrar(@RequestBody ClienteDTO cliente) {
        Cliente salvo = clienteService.adicionarCliente(cliente);
        return ResponseEntity.status(HttpStatus.CREATED).body(ClienteRespostaDTO.criar(salvo));
    }

    //@GetMapping
    @ResponseBody
    @RequestMapping(path = "/listar", method = RequestMethod.GET)
    public ResponseEntity<Map<String, Object>> listar(@RequestParam int start, @RequestParam int limit) {
        return ResponseEntity.ok(ClienteRespostaDTO.listar(clienteService.buscarTodos(start, limit)));
    }

    @GetMapping("/teste")
    public ResponseEntity<String> teste() {
        return ResponseEntity.ok("Ok");
    }
}