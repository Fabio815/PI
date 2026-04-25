package br.com.sistemaos.applicationservice;

import br.com.sistemaos.domain.entity.Cliente;
import br.com.sistemaos.domain.entity.Endereco;
import br.com.sistemaos.domain.model.Filtro;
import br.com.sistemaos.domain.model.Status;
import br.com.sistemaos.domain.model.TipoFiltro;
import br.com.sistemaos.dto.ClienteDTO;
import br.com.sistemaos.dto.ClienteRespostaDTO;
import br.com.sistemaos.repository.ClienteRepository;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service // Classe Server - dados no banco
@RequiredArgsConstructor // Lombok - cria um construtor com todos os parametros
@RequestMapping("/cliente")
@Slf4j //Isso aqui é para o looger...
public class ClienteService {
    //private static final Logger LOGGER = LoggerFactory.getLogger(ClienteService.class);
    private final ClienteRepository clienteRepository;

    public Map<String, Object> buscarTodos(int start, int limit, String filtros) {
        ObjectMapper mapper = new ObjectMapper();
        List<Filtro> listaFiltros = new ArrayList<>();
        if (filtros != null) {
            listaFiltros = mapper.readValue(filtros, new TypeReference<List<Filtro>>() {});
        }

        String valorFiltro = null;
        byte tipoFiltro = -1;

        for (Filtro filtro : listaFiltros) {
            switch (filtro.getOperador()) {
                case "like":
                    valorFiltro = filtro.getValor();
                    tipoFiltro = TipoFiltro.NOME;
                    break;
                case "eq":
                    valorFiltro = filtro.getValor();
                    tipoFiltro = TipoFiltro.ID;
                    break;
                case "in":
                    valorFiltro = filtro.getValor();
                    tipoFiltro = TipoFiltro.CHECKCOLUMN;
                    break;
                default:
                    break;
            }
        }

        int page = start / limit;
        Pageable pageable = PageRequest.of(page, limit);

        Page<Cliente> dados = null;
        if (valorFiltro != null && !valorFiltro.isBlank()) {
            switch (tipoFiltro) {
                case TipoFiltro.NOME:
                    dados = clienteRepository.findByNomeContainingIgnoreCase(valorFiltro, pageable);
                    break;
                case TipoFiltro.ID:
                    dados = clienteRepository.findById(Long.parseLong(valorFiltro), pageable);
                    break;
                case TipoFiltro.CHECKCOLUMN:
                    dados = clienteRepository.findByStatus(Status.valueOf(valorFiltro), pageable);
                    break;
                default:
                    break;
            }
        } else {
            dados = clienteRepository.findAll(pageable);
        }
        return ClienteRespostaDTO.converter(dados);
    }

    //Essa marcação serve para que tudo seja feito, ou nada seja feito, caso dê ruim na transação ele cancela;
    @Transactional
    public Cliente adicionarCliente(ClienteDTO cliente) {
        if (cliente == null) {
            log.info("O Cliente não pode ser nulo");
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
        log.info("Cliente adicionado com sucesso {}", salvo);
        return salvo;
    }
}


