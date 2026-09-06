package br.com.sistemaos.infraestrura.dto;

import br.com.sistemaos.domain.entity.Cliente;
import br.com.sistemaos.domain.model.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.Objects;

@Data
@AllArgsConstructor
@Builder
public class ClienteDTO {
    private final Long id;
    private final String nome;
    private final String telefone;
    private final Status status;
    private final EnderecoDTO endereco;
    //private List<Os> ordensServico;

    public static ClienteDTO criar(Cliente cliente) {
        EnderecoDTO enderecoDTO = null;
        if (!Objects.isNull(cliente.getEndereco())) {
            enderecoDTO = EnderecoDTO.builder()
                    .id(cliente.getEndereco().getId())
                    .rua(cliente.getEndereco().getRua())
                    .complemento(cliente.getEndereco().getComplemento())
                    .numero(cliente.getEndereco().getNumero())
                    .logradouro(cliente.getEndereco().getLogradouro())
                    .build();
        }
        return new ClienteDTO(
                cliente.getId(),
                cliente.getNome(),
                cliente.getTelefone(),
                cliente.getStatus(),
                enderecoDTO
        );
    }
}