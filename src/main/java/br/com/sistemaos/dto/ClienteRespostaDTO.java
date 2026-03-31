package br.com.sistemaos.dto;

import br.com.sistemaos.domain.entity.Cliente;
import br.com.sistemaos.domain.entity.Endereco;
import br.com.sistemaos.domain.entity.Os;
import br.com.sistemaos.domain.model.Status;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClienteRespostaDTO {
    private Long id;
    private String nome;
    private String telefone;
    private Status status;
    private EnderecoDTO endereco;
    private List<Os> ordensServico;

    public static ClienteRespostaDTO criar(Cliente cliente) {
        EnderecoDTO enderecoDTO = new EnderecoDTO();
        enderecoDTO.setId(cliente.getEndereco().getId());
        enderecoDTO.setRua(cliente.getEndereco().getRua());
        enderecoDTO.setNumero(cliente.getEndereco().getNumero());
        enderecoDTO.setLogradouro(cliente.getEndereco().getLogradouro());
        enderecoDTO.setComplemento(cliente.getEndereco().getComplemento());

        ClienteRespostaDTO resposta = new ClienteRespostaDTO();
        resposta.setId(cliente.getId());
        resposta.setNome(cliente.getNome());
        resposta.setTelefone(cliente.getTelefone());
        resposta.setStatus(cliente.getStatus());
        resposta.setEndereco(enderecoDTO);
        return resposta;
    }
}
