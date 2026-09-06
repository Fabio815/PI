package br.com.sistemaos.domain.applicationservice;

import br.com.sistemaos.domain.entity.Cliente;
import br.com.sistemaos.domain.entity.Endereco;
import br.com.sistemaos.domain.exception.ClienteNaoEncontradoException;
import br.com.sistemaos.domain.exception.ConverteStatusException;
import br.com.sistemaos.domain.exception.NumeroTelefoneExistenteException;
import br.com.sistemaos.domain.model.Status;
import br.com.sistemaos.domain.repository.ClienteRepository;
import br.com.sistemaos.infraestrura.dto.ClienteDTO;
import br.com.sistemaos.infraestrura.dto.SalvarClienteDTO;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.*;

@Service // Classe Server - dados no banco
@RequiredArgsConstructor // Lombok - cria um construtor com todos os parametros
@RequestMapping("/cliente")
@Slf4j //Isso aqui é para o looger...
public class ClienteService {
    //private static final Logger LOGGER = LoggerFactory.getLogger(ClienteService.class);
    private final ClienteRepository clienteRepository;

    //Essa marcação serve para que tudo seja feito, ou nada seja feito, caso dê ruim na transação ele cancela;
    @Transactional
    public Cliente adicionarCliente(SalvarClienteDTO clienteDTO) {
        if (telefoneExiste(null, clienteDTO.getTelefone())) {
            throw new NumeroTelefoneExistenteException(clienteDTO.getTelefone());
        }
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

    public Map<String, Object> listarClientes(Long id, String nome, List<String> status, Pageable pageable) {
        Page<Cliente> listaClientes;
        if (!Objects.isNull(id) || !Objects.isNull(nome) || !Objects.isNull(status)) {
            listaClientes = clienteRepository.listarClientes(nome, converterParaStatusList(status), id, pageable);
        } else {
            listaClientes = clienteRepository.findAll(pageable);
        }
        return carregarObjetoTelefone(listaClientes);
    }

    /*public Map<String, Object> listarClienteOs(String nome, Pageable pageable) {
        Page<Cliente> listaClientes;

        listaClientes = clienteRepository.listarClientes(nome, Status.ATIVO, null, pageable);
        return carregarObjetoTelefone(listaClientes);
    }*/

    @Transactional
    public Cliente atualizarCliente(Long id, SalvarClienteDTO salvarClienteDTO) {
        if (telefoneExiste(id, salvarClienteDTO.getTelefone())) {
            throw new NumeroTelefoneExistenteException(salvarClienteDTO.getTelefone());
        }
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

    @Transactional
    public Cliente atualizarStatus(Long id) {
        Cliente cliente = carregarCliente(id);
        cliente.setStatus(trocarStatus(cliente));

        return cliente;
    }

    private boolean telefoneExiste(Long id, String telefone) {
        Cliente cliente = clienteRepository.findByTelefone(telefone);
        if (Objects.isNull(cliente)) {
            return false;
        }
        if (cliente.getId().equals(id)) {
            return false;
        }
        return true;
    }

    public Cliente carregarCliente(Long id) {
        Optional<Cliente> op = clienteRepository.findById(id);
        if (op.isEmpty()) {
            throw new ClienteNaoEncontradoException(id);
        }
        return op.get();
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

    private Status trocarStatus(Cliente cliente) {
        if (cliente.getStatus() == Status.ATIVO) {
            return Status.INATIVO;
        } else {
            return  Status.ATIVO;
        }
    }

    @NonNull
    private Map<String, Object> carregarObjetoTelefone(Page<Cliente> listaClientes) {
        List<ClienteDTO> valor = listaClientes.getContent().stream()
                .map(ClienteDTO::criar)
                .toList();

        Map<String, Object> resposta = new HashMap<>();
        resposta.put("listaClientes", valor);
        resposta.put("total", listaClientes.getTotalElements());
        return resposta;
    }
}