package br.com.sistemaos.infraestrura.dto;

import br.com.sistemaos.domain.applicationservice.OsService;
import br.com.sistemaos.domain.entity.HistoricoOs;
import br.com.sistemaos.domain.entity.Os;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Builder
public class HistoricoOsDTO {
    private Long id;
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