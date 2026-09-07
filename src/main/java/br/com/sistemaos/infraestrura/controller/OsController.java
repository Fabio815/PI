package br.com.sistemaos.infraestrura.controller;

import br.com.sistemaos.domain.applicationservice.OsService;
import br.com.sistemaos.domain.entity.Os;
import br.com.sistemaos.infraestrura.dto.OsDTO;
import br.com.sistemaos.infraestrura.dto.SalvarOsDTO;
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

//Criado 02/09/26
@RestController
@RequestMapping("/os") //Ordem de Serviço
@AllArgsConstructor
@Slf4j
public class OsController {
    private final OsService osService;

    @PostMapping("/cadastrar") //Cadastrar a OS
    public ResponseEntity<OsDTO> cadastrar(@RequestBody @Valid SalvarOsDTO salvarOsDTO) { //Recebe os dados e executa a validação
        Os os = osService.adicionarOs(salvarOsDTO); //Cria e salva
        return ResponseEntity.created(URI.create("/os/" + os.getId())).body(OsDTO.criar(os));
    }

    @GetMapping("/listar") //Responsavel pela listagem da OS
    public ResponseEntity<Map<String, Object>> listar(
            @RequestParam(value = "id", required = false) long id,
            @RequestParam(value = "status", required = false) List<String> status,
            @RequestParam(defaultValue = "0") int start,
            @RequestParam(defaultValue = "25") int limit) {

        int page = start / limit;
        Pageable pageable = PageRequest.of(page, limit);
        Map<String, Object> listaOs = osService.listarOs(id, status, pageable);
        return ResponseEntity.ok(listaOs);
    }

    public ResponseEntity<OsDTO> carregarPorId(long id) {
        Os os = osService.carregarPorId(id);
        return null;
    }
}