package br.com.sistemaos.domain.applicationservice;

import br.com.sistemaos.domain.entity.Cliente;
import br.com.sistemaos.domain.entity.Endereco;
import br.com.sistemaos.domain.entity.Produto;
import br.com.sistemaos.domain.exception.ClienteNaoEncontradoException;
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
        if (!Objects.isNull(id) || !Objects.isNull(nome) || !Objects.isNull(status)) {
            Page<Cliente> clientes = clienteRepository.listarClientes(nome, converterParaStatusList(status), id, pageable);
            if (!clientes.isEmpty()) {
                for (Cliente cl : clientes) {
                    listaClientes.add(cl);
                }
            }
        } else {
            Page<Cliente> clientes = clienteRepository.findAllNotInativo(pageable);
            for (Cliente cl : clientes) {
                listaClientes.add(cl);
            }
        }
        return listaClientes;
    }

    @Transactional
    public Cliente atualizarCliente(Long id, SalvarClienteDTO salvarClienteDTO) {
        Cliente cliente = carregarCliente(id);

        Endereco endereco = Endereco.builder()
                .rua(salvarClienteDTO.getEndereco().getRua())
                .numero(salvarClienteDTO.getEndereco().getNumero())
                .logradouro(salvarClienteDTO.getEndereco().getLogradouro())
                .complemento(salvarClienteDTO.getEndereco().getComplemento())
                .build();

        cliente.setNome(salvarClienteDTO.getNome());
        cliente.setTelefone(salvarClienteDTO.getTelefone());
        cliente.setStatus(Status.valueOf(salvarClienteDTO.getStatus()));
        cliente.setEndereco(endereco);

        return cliente;
    }

    public Cliente carregarCliente(Long id) {
        Optional<Cliente> op = clienteRepository.findById(id);
        if (op.isEmpty()) {
            throw new ClienteNaoEncontradoException(id);
        }
        return op.get();
    }

    @Transactional
    public Cliente statusCliente(Long id, SalvarClienteDTO salvarClienteDTO) {
        Cliente cliente = carregarCliente(id);
        cliente.setStatus(Status.valueOf(salvarClienteDTO.getStatus()));

        return cliente;
    }

    private List<Status> converterParaStatusList(List<String> status) {
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