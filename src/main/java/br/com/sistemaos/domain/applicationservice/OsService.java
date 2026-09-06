package br.com.sistemaos.domain.applicationservice;

import br.com.sistemaos.domain.entity.Cliente;
import br.com.sistemaos.domain.entity.Os;
import br.com.sistemaos.domain.entity.Usuario;
import br.com.sistemaos.domain.model.StatusOs;
import br.com.sistemaos.domain.repository.ClienteRepository;
import br.com.sistemaos.domain.repository.OsRepository;
import br.com.sistemaos.domain.repository.UsuarioRepository;
import br.com.sistemaos.infraestrura.dto.OsDTO;
import br.com.sistemaos.infraestrura.dto.SalvarOsDTO;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class OsService {

    private final OsRepository osRepository;
    private final ClienteRepository clienteRepository;
    private final UsuarioRepository usuarioRepository;
    private java.lang.Object LocalDate;

    @Transactional
    public Os adicionarOs(SalvarOsDTO salvarOsDTO) {

        Cliente cliente = clienteRepository
                .findById(salvarOsDTO.getCliente().getId())
                .orElseThrow();

        Usuario usuario = usuarioRepository
                .findById(salvarOsDTO.getUsuario().getId())
                .orElseThrow();

        Os os = new Os();

        os.setCliente(cliente);
        os.setUsuario(usuario);
        os.setStatus(salvarOsDTO.getStatus());
        os.setDataEmissao(LocalDate.now());

        osRepository.save(os);

        return os;
    }

    public Map<String, Object> listarOs(
            Long id,
            List<String> status,
            Pageable pageable) {

        Page<Os> listaOs;

        listaOs = osRepository.findAll(pageable);

        List<OsDTO> valor = listaOs.getContent()
                .stream()
                .map(OsDTO::criar)
                .toList();

        Map<String, Object> resposta = new HashMap<>();

        resposta.put("listaOs", valor);
        resposta.put("total", listaOs.getTotalElements());

        return resposta;
    }
}