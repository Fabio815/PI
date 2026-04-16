package br.com.sistemaos.dto;

import br.com.sistemaos.domain.entity.Cliente;
import br.com.sistemaos.domain.entity.Endereco;
import br.com.sistemaos.domain.entity.Os;
import br.com.sistemaos.domain.model.Status;
import br.com.sistemaos.repository.ClienteRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
public class ClienteRespostaDTO {
    private Long id;
    private String nome;
    private String telefone;
    private Status status;
    private EnderecoDTO endereco;
    //private List<Os> ordensServico;

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

    public static Map<String, Object> converter(Page<Cliente> dados) {
        if (dados == null) { return null; }

        List<ClienteRespostaDTO> lista = new ArrayList<>();

        for (Cliente c : dados.getContent()) {
            EnderecoDTO enderecoDTO = new EnderecoDTO();
            ClienteRespostaDTO clienteDTO = new ClienteRespostaDTO();
            clienteDTO.setId(c.getId());
            clienteDTO.setNome(c.getNome());
            clienteDTO.setTelefone(c.getTelefone());
            clienteDTO.setStatus(c.getStatus());

            if (c.getEndereco() != null) {
                enderecoDTO.setId(c.getEndereco().getId());
                enderecoDTO.setRua(c.getEndereco().getRua());
                enderecoDTO.setNumero(c.getEndereco().getNumero());
                enderecoDTO.setLogradouro(c.getEndereco().getLogradouro());
                enderecoDTO.setComplemento(c.getEndereco().getComplemento());
                clienteDTO.setEndereco(enderecoDTO);
            }

            lista.add(clienteDTO);
        }

        Map<String, Object> retorno = new HashMap<>();
        retorno.put("clientes", lista);
        retorno.put("total", dados.getTotalElements());
        return retorno;
    }
}
