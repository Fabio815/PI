package br.com.sistemaos.infraestrura.controller;

import br.com.sistemaos.domain.applicationservice.ClienteService;
import br.com.sistemaos.domain.model.Resposta;
import br.com.sistemaos.infraestrura.dto.ClienteDTO;
import br.com.sistemaos.infraestrura.dto.ClienteRespostaDTO;
import br.com.sistemaos.infraestrura.dto.SalvarClienteDTO;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<ClienteRespostaDTO> cadastrar(@RequestBody @Valid SalvarClienteDTO cliente) {

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