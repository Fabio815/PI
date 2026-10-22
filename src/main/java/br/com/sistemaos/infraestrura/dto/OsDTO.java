package br.com.sistemaos.infraestrura.dto;

import br.com.sistemaos.domain.entity.Os;
import br.com.sistemaos.domain.entity.Usuario;
import br.com.sistemaos.domain.model.Status;
import br.com.sistemaos.domain.model.StatusOs;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.Objects;

@Data
@AllArgsConstructor
@Builder
public class OsDTO {
    private final Long id;
    private final LocalDate dataEmissao;
    private final StatusOs situacao;
    private final UsuarioDTO usuario;
    private final ClienteDTO cliente;
    private final OrcamentoDTO orcamento;
    private final Status status;
    private final String modelo;
    private final String cor;
    private final UsuarioRefDTO responsavel;
    private final LocalDate dataFim;
    private final boolean encerrada;

    public OsDTO(Long id, LocalDate dataEmissao, StatusOs situacao, UsuarioDTO usuario,
                 ClienteDTO cliente, OrcamentoDTO orcamento, Status status, String modelo, String cor) {
        this(id, dataEmissao, situacao, usuario, cliente, orcamento, status, modelo, cor, null, null,
                situacao == StatusOs.CONCLUIDO || situacao == StatusOs.CANCELADO);
    }

    public static OsDTO criar(Os os) {
        UsuarioDTO usuarioDTO = UsuarioDTO.criar(os.getUsuario());
        ClienteDTO clienteDTO = ClienteDTO.criar(os.getCliente());
        OrcamentoDTO orcamentoDTO = !Objects.isNull(os.getOrcamento()) ? OrcamentoDTO.criar(os.getOrcamento()) : null;
        Usuario responsavelEfetivo = os.getResponsavel() != null ? os.getResponsavel() : os.getUsuario();
        boolean encerrada = os.getDataFim() != null || os.getSituacao() == StatusOs.CONCLUIDO || os.getSituacao() == StatusOs.CANCELADO;

        return new OsDTO(os.getId(), os.getDataEmissao(), os.getSituacao(), usuarioDTO, clienteDTO,
                orcamentoDTO, os.getStatus(), os.getModelo(), os.getCor(),
                UsuarioRefDTO.criar(responsavelEfetivo), os.getDataFim(), encerrada);
    }
}
