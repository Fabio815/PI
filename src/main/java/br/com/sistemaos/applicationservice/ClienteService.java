package br.com.sistemaos.applicationservice;

import br.com.sistemaos.repository.ClienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service //classe Server - dados no banco
@RequiredArgsConstructor //lombok -cria um construtor com todos os parametros


public class ClienteService {
    private ClienteRepository clienteRepository;
    public static void adicionarCliente() {

    }
}
