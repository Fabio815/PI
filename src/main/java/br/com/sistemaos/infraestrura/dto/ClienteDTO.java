package br.com.sistemaos.infraestrura.dto;

import br.com.sistemaos.domain.entity.Cliente;
import br.com.sistemaos.domain.model.Status;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClienteDTO {
    private Long id;
    private String nome;
    private String telefone;
    private Status status;
    private EnderecoDTO endereco;
    //private List<Os> ordensServico;

    public ClienteDTO criar(Cliente cliente) {
        EnderecoDTO enderecoDTO = EnderecoDTO.builder()
                .id(cliente.getEndereco().getId())
                .rua(cliente.getEndereco().getRua())
                .complemento(cliente.getEndereco().getComplemento())
                .numero(cliente.getEndereco().getNumero())
                .logradouro(cliente.getEndereco().getLogradouro())
                .build();
        return new ClienteDTO(
                cliente.getId(),
                cliente.getNome(),
                cliente.getTelefone(),
                cliente.getStatus(),
                enderecoDTO
        );
    }
}