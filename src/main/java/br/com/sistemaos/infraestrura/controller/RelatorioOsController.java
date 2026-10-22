package br.com.sistemaos.infraestrura.controller;

import br.com.sistemaos.domain.applicationservice.OsRelatorioService;
import br.com.sistemaos.domain.model.Status;
import br.com.sistemaos.domain.model.StatusOs;
import br.com.sistemaos.infraestrura.dto.RelatorioOsRespostaDTO;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/os")
public class RelatorioOsController {
    private final OsRelatorioService relatorioService;

    public RelatorioOsController(OsRelatorioService relatorioService) {
        this.relatorioService = relatorioService;
    }

    @GetMapping("/relatorio")
    public ResponseEntity<RelatorioOsRespostaDTO> consultar(
            @RequestParam(value = "dataInicio", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam(value = "dataFim", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim,
            @RequestParam(value = "clienteId", required = false) Long clienteId,
            @RequestParam(value = "situacao", required = false) StatusOs situacao,
            @RequestParam(value = "status", required = false) Status status,
            @RequestParam(value = "responsavelId", required = false) Long responsavelId,
            @RequestParam(value = "start", defaultValue = "0") int start,
            @RequestParam(value = "limit", defaultValue = "25") int limit) {
        return ResponseEntity.ok(relatorioService.consultar(
                dataInicio, dataFim, clienteId, situacao, status, responsavelId, start, limit));
    }

    /** Os filtros do relatório são locais: a advice legada traduz exceções genéricas em 500. */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> parametroInvalido(IllegalArgumentException exception) {
        return respostaInvalida(exception.getMessage());
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, Object>> tipoDeParametroInvalido(MethodArgumentTypeMismatchException exception) {
        return respostaInvalida("Parâmetro inválido: " + exception.getName() + ".");
    }

    private ResponseEntity<Map<String, Object>> respostaInvalida(String mensagem) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("codigoErro", "FILTRO_RELATORIO_INVALIDO");
        body.put("mensagemErro", mensagem == null ? "Parâmetros do relatório inválidos." : mensagem);
        body.put("status", HttpStatus.BAD_REQUEST.value());
        return ResponseEntity.badRequest().body(body);
    }
}
