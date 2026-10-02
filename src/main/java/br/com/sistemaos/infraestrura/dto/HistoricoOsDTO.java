package br.com.sistemaos.infraestrura.dto;

import br.com.sistemaos.domain.applicationservice.OsService;
import br.com.sistemaos.domain.entity.HistoricoOs;
import br.com.sistemaos.domain.entity.Os;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Builder
public class HistoricoOsDTO {
    private Long id;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime dataAlteracao;
    private String campoAlterado;
    private String de;
    private String para;
    private UsuarioDTO usuario;
    private OsDTO os;

    public static HistoricoOsDTO criar(HistoricoOs historicoOs) {
        UsuarioDTO usuarioDTO = UsuarioDTO.criar(historicoOs.getUsuario());
        OsDTO osDTO = OsDTO.criar(historicoOs.getOs());
        return new HistoricoOsDTO(
                historicoOs.getId(),
                historicoOs.getDataAlteracao(),
                historicoOs.getCampoAlterado(),
                historicoOs.getDe(),
                historicoOs.getPara(),
                usuarioDTO,
                osDTO
        );
    }
}