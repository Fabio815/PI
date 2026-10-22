package br.com.sistemaos.infraestrura.dto;

import br.com.sistemaos.domain.entity.HistoricoOs;
import br.com.sistemaos.domain.model.TipoEventoHistorico;
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
    private TipoEventoHistorico tipoEvento;
    private String descricao;
    private UsuarioRefDTO responsavelAnterior;
    private UsuarioRefDTO novoResponsavel;

    /** Compatibilidade binária-fonte com o antigo formato de histórico. */
    public HistoricoOsDTO(Long id, LocalDateTime dataAlteracao, String campoAlterado,
                          String de, String para, UsuarioDTO usuario, OsDTO os) {
        this(id, dataAlteracao, campoAlterado, de, para, usuario, os, null, null, null, null);
    }

    public static HistoricoOsDTO criar(HistoricoOs h) {
        return criar(h, h.getTipoEvento(), h.getDescricao());
    }

    public static HistoricoOsDTO criar(HistoricoOs h, TipoEventoHistorico tipoEfetivo, String descricaoEfetiva) {
        return new HistoricoOsDTO(h.getId(), h.getDataAlteracao(), h.getCampoAlterado(), h.getDe(), h.getPara(),
                UsuarioDTO.criar(h.getUsuario()), h.getOs() == null ? null : OsDTO.criar(h.getOs()),
                tipoEfetivo, descricaoEfetiva,
                UsuarioRefDTO.criar(h.getResponsavelAnterior()), UsuarioRefDTO.criar(h.getNovoResponsavel()));
    }
}
