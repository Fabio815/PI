package br.com.sistemaos.applicationservice;

import br.com.sistemaos.domain.entity.Cliente;
import br.com.sistemaos.repository.ClienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import java.util.List;

@Service // Classe Server - dados no banco
@RequiredArgsConstructor // Lombok - cria um construtor com todos os parametros

public class ClienteService {
    private final ClienteRepository clienteRepository;

    public List<Cliente> buscarTodos() {
        // O findAll() é um metodo padrão do JpaRepository que traz tudo do banco
        return clienteRepository.findAll();
    }

    public Cliente adicionarCliente(Cliente cliente) {
        //Ultima modificação 23/03/26
        if (cliente.getEndereco() != null) { // Garante que endereço tenha cliente
            cliente.getEndereco().setCliente(cliente);
        }

        return clienteRepository.save(cliente); // Retorna o repositório do cliente já salvo
        }
    }


