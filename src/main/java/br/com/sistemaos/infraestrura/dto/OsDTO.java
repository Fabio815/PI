package br.com.sistemaos.infraestrura.dto;

import br.com.sistemaos.domain.entity.Orcamento;
import br.com.sistemaos.domain.entity.Os;
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
    private final StatusOs status;
    private final UsuarioDTO usuario;
    private final ClienteDTO cliente;
    private final OrcamentoDTO orcamento;

    public static OsDTO criar(Os os) {
        UsuarioDTO usuarioDTO = UsuarioDTO.criar(os.getUsuario());
        ClienteDTO clienteDTO = ClienteDTO.criar(os.getCliente());
        OrcamentoDTO orcamentoDTO = !Objects.isNull(os.getOrcamento()) ? OrcamentoDTO.criar(os.getOrcamento()) : null;

        return new OsDTO(
                os.getId(),
                os.getDataEmissao(),
                os.getStatus(),
                usuarioDTO,
                clienteDTO,
                orcamentoDTO
        );
    }
}
