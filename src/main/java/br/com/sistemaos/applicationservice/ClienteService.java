package br.com.sistemaos.applicationservice;

import br.com.sistemaos.domain.entity.Cliente;
import br.com.sistemaos.domain.entity.Endereco;
import br.com.sistemaos.domain.model.Filtro;
import br.com.sistemaos.domain.model.Resposta;
import br.com.sistemaos.domain.model.Status;
import br.com.sistemaos.domain.model.TipoFiltro;
import br.com.sistemaos.dto.ClienteDTO;
import br.com.sistemaos.dto.ClienteRespostaDTO;
import br.com.sistemaos.repository.ClienteCostumizadoRepository;
import br.com.sistemaos.repository.ClienteRepository;
import br.com.sistemaos.repository.EnderecoRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
    public ClienteRespostaDTO adicionarCliente(ClienteDTO cliente) {
        ClienteRespostaDTO resposta = new ClienteRespostaDTO();

        if (cliente == null) {
            log.info("O Cliente não pode ser nulo");
            resposta.setResposta(Resposta.falha("O Cliente não pode ser nulo"));
            return null;
        }

        Cliente salvo = Cliente.builder()
                .nome(cliente.getNome())
                .telefone(cliente.getTelefone())
                .status(cliente.getStatus())
                .build();

        Endereco endereco = Endereco.builder()
                .rua(cliente.getEndereco().getRua())
                .numero(cliente.getEndereco().getNumero())
                .logradouro(cliente.getEndereco().getLogradouro())
                .complemento(cliente.getEndereco().getComplemento())
                .cliente(salvo).build();

        salvo.setEndereco(endereco);

        clienteRepository.save(salvo);

        resposta = ClienteRespostaDTO.criar(salvo);
        resposta.setResposta(Resposta.sucesso("Cliente cadstrado com sucesso!"));
        return resposta;
    }

    public Map<String, Object> buscarTodos(int start, int limit, String filtros) {
        ObjectMapper mapper = new ObjectMapper();
        List<Filtro> listaFiltros = new ArrayList<>();
        if (filtros != null) {
            listaFiltros = mapper.readValue(filtros, new TypeReference<List<Filtro>>() {});
        }

        //List<Cliente> dados = listagemCostumizadaRepository.listagemClientes(listaFiltros);
        int page = start / limit;
        Pageable pageable = PageRequest.of(page, limit);
        Page<Cliente> dados = listagemCostumizadaRepository.listagemClientes(listaFiltros, pageable);

        return ClienteRespostaDTO.converter(dados);
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
}