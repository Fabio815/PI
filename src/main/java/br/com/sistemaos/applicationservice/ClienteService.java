package br.com.sistemaos.applicationservice;

import br.com.sistemaos.controller.ClienteController;
import br.com.sistemaos.domain.entity.Cliente;
import br.com.sistemaos.domain.entity.Endereco;
import br.com.sistemaos.domain.entity.Os;
import br.com.sistemaos.domain.model.Status;
import br.com.sistemaos.dto.ClienteDTO;
import br.com.sistemaos.dto.ClienteRespostaDTO;
import br.com.sistemaos.repository.ClienteRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Service // Classe Server - dados no banco
@RequiredArgsConstructor // Lombok - cria um construtor com todos os parametros
@RequestMapping("/cliente")
@Slf4j //Isso aqui é para o looger...
public class ClienteService {
    //private static final Logger LOGGER = LoggerFactory.getLogger(ClienteService.class);
    private final ClienteRepository clienteRepository;

    public List<Cliente> buscarTodos() {
        // O findAll() é um metodo padrão do JpaRepository que traz tudo do banco
        return clienteRepository.findAll();
    }

    //Essa marcação serve para que tudo seja feito ou nada seja feito, caso dê ruim na transação ele cancela;
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


