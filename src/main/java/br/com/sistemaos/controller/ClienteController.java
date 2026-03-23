package br.com.sistemaos.controller;

import br.com.sistemaos.dto.Cliente;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ClienteController {
/*    @PostMapping
    public ResponseEntity <String> adicionarCliente(@RequestBody Cliente) {
        return ResponseEntity.ok("Ok");
    }
*/

    @GetMapping("/teste")
    public ResponseEntity<String> teste() {
        return ResponseEntity.ok("Ok");
    }
}
