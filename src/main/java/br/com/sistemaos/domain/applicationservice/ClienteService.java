package br.com.sistemaos.domain.applicationservice;

import br.com.sistemaos.domain.entity.Cliente;
import br.com.sistemaos.domain.entity.Endereco;
import br.com.sistemaos.domain.entity.Produto;
import br.com.sistemaos.domain.exception.ConverteStatusException;
import br.com.sistemaos.domain.model.Filtro;
import br.com.sistemaos.domain.model.Resposta;
import br.com.sistemaos.domain.model.Status;
import br.com.sistemaos.infraestrura.dto.ClienteDTO;
import br.com.sistemaos.infraestrura.dto.ClienteRespostaDTO;
import br.com.sistemaos.domain.repository.ClienteCostumizadoRepository;
import br.com.sistemaos.domain.repository.ClienteRepository;
import br.com.sistemaos.domain.repository.EnderecoRepository;
import br.com.sistemaos.infraestrura.dto.SalvarClienteDTO;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.util.*;

@Service // Classe Server - dados no banco
@RequiredArgsConstructor // Lombok - cria um construtor com todos os parametros
@RequestMapping("/cliente")
@Slf4j //Isso aqui é para o looger...
public class ClienteService {
    //private static final Logger LOGGER = LoggerFactory.getLogger(ClienteService.class);
    private final ClienteRepository clienteRepository;
    private final EnderecoRepository enderecoRepository;
    private final ClienteCostumizadoRepository listagemCostumizadaRepository;

    //Essa marcação serve para que tudo seja feito, ou nada seja feito, caso dê ruim na transação ele cancela;
    @Transactional
    public Cliente adicionarCliente(SalvarClienteDTO clienteDTO) {
        Endereco endereco = Endereco.builder()
                .complemento(clienteDTO.getEndereco().getComplemento())
                .logradouro(clienteDTO.getEndereco().getLogradouro())
                .numero(clienteDTO.getEndereco().getNumero())
                .numero(clienteDTO.getEndereco().getNumero())
                .rua(clienteDTO.getEndereco().getRua())
                .build();

        Cliente cliente = Cliente.builder()
                .nome(clienteDTO.getNome())
                .telefone(clienteDTO.getTelefone())
                .endereco(endereco)
                .status(Status.ATIVO)
                .build();

        clienteRepository.save(cliente);
        return cliente;
    }

    public List<Cliente> listarClientes(Long id, String nome, List<String> status, Pageable pageable) {
        List<Cliente> listaClientes = new ArrayList<>();
        Page<Cliente> clientes = clienteRepository.listarClientes(nome, converterParaStatus(status), id, pageable);

        if (!clientes.isEmpty()) {
            for (Cliente cl : clientes) {
                listaClientes.add(cl);
            }
        }
        return listaClientes;
    }

    public ClienteRespostaDTO atualizarClienteId(ClienteDTO cliente, Long id) {
        Optional<Cliente> clienteOp = clienteRepository.findById(id);
        if (clienteOp.isPresent()) {
            Cliente clienteExistente = clienteOp.get();
            Endereco endereco = null;

            if (cliente.getEndereco() != null) {
                if (cliente.getEndereco().getId() == null) {
                    endereco = Endereco.builder()
                            .rua(cliente.getEndereco().getRua())
                            .numero(cliente.getEndereco().getNumero())
                            .logradouro(cliente.getEndereco().getLogradouro())
                            .complemento(cliente.getEndereco().getComplemento())
                            .cliente(clienteExistente)
                            .build();
                    endereco = enderecoRepository.save(endereco);
                } else {
                    enderecoRepository.updateEndereco(
                            cliente.getEndereco().getComplemento(),
                            cliente.getEndereco().getLogradouro(),
                            cliente.getEndereco().getNumero(),
                            cliente.getEndereco().getRua(),
                            cliente.getEndereco().getId());

                    endereco = Endereco.builder()
                            .id(cliente.getEndereco().getId())
                            .rua(cliente.getEndereco().getRua())
                            .numero(cliente.getEndereco().getNumero())
                            .logradouro(cliente.getEndereco().getLogradouro())
                            .complemento(cliente.getEndereco().getComplemento())
                            .cliente(clienteExistente)
                            .build();
                }
            }

            // updateCliente só precisa atualizar nome e telefone agora
            clienteRepository.updateCliente(cliente.getNome(), cliente.getTelefone(), id);

            Cliente cl = Cliente.builder()
                    .id(id)
                    .nome(cliente.getNome())
                    .telefone(cliente.getTelefone())
                    .endereco(endereco)
                    .build();

            ClienteRespostaDTO reposta = ClienteRespostaDTO.criar(cl);
            reposta.setResposta(Resposta.sucesso("Cliente atualizado com sucesso!"));
            return reposta;
        }
        return null;
    }

    @Transactional
    public Resposta atualizarStatus(ClienteDTO cliente) {
        Resposta resposta;
        if (cliente == null) {
            resposta = Resposta.falha("Erro ao tentar atualizar os dados!");
            return resposta;
        }
        Status status = cliente.getStatus().equals(Status.ATIVO) ? Status.INATIVO : Status.ATIVO;
        clienteRepository.udpateStatus(status, cliente.getId());
        resposta = Resposta.sucesso("Cliente atualizado com sucesso!");
        return resposta;
    }

    private List<Status> converterParaStatus(List<String> status) {
        if (status == null) {
            return null;
        }
        try {
            return status.stream().map(s -> Status.valueOf(s)).toList();
        } catch (IllegalArgumentException | NullPointerException e) {
            throw new ConverteStatusException(status.toString());
        }
    }
}