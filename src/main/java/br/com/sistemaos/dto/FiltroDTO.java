package br.com.sistemaos.dto;

import br.com.sistemaos.domain.model.Filtro;
import lombok.Data;

import java.util.List;

@Data
public class FiltroDTO {
    private int start;
    private int limit;
    private List<Filtro> filtros;
}
