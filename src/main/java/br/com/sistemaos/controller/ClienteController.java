package br.com.sistemaos.controller;

import br.com.sistemaos.applicationservice.ClienteService;
import br.com.sistemaos.domain.entity.Cliente;
import br.com.sistemaos.domain.model.Filtro;
import br.com.sistemaos.domain.model.Resposta;
import br.com.sistemaos.dto.ClienteDTO;
import br.com.sistemaos.dto.ClienteRespostaDTO;
import br.com.sistemaos.dto.FiltroDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

//Ultima modificação 23/03/26
@RestController
@RequestMapping("/cliente") //Teste do banco de dados
@Slf4j
public class ClienteController {

    private final ClienteService clienteService;

    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    //@PostMapping
    @ResponseBody
    @RequestMapping(path = "/cadastrar", method = RequestMethod.POST)
    //Cadastro do cliente
    public ResponseEntity<ClienteRespostaDTO> cadastrar(@RequestBody @Valid ClienteDTO cliente) {
        return ResponseEntity.status(HttpStatus.CREATED).body(clienteService.adicionarCliente(cliente));
    }

    //@GetMapping
    @RequestMapping(path = "/listar", method = RequestMethod.GET)
    public ResponseEntity<Map<String, Object>> listar(@RequestParam(value = "start") int start,
            @RequestParam(value = "limit") int limit,
            @RequestParam(value = "filtros") String filtros) {
        return ResponseEntity.ok(clienteService.buscarTodos(start, limit, filtros));
    }

    @RequestMapping(path = "/atualizar/{id}", method = RequestMethod.PUT)
    public ResponseEntity<ClienteRespostaDTO> atualizar(@RequestBody ClienteDTO cliente, @PathVariable Long id) {
        return ResponseEntity.ok().body(clienteService.atualizarClienteId(cliente, id));
    }

    @RequestMapping(path = "/status/{id}/{status}", method = RequestMethod.PUT)
    public ResponseEntity<Resposta> atualizarStatus(@PathVariable Long id, @PathVariable String status) {
        return ResponseEntity.ok().body(clienteService.atualizarStatus(status, id));
    }



    @GetMapping("/teste")
    public ResponseEntity<String> teste() {
        return ResponseEntity.ok("Ok");
    }
}