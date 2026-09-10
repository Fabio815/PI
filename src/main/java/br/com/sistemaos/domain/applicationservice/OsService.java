package br.com.sistemaos.domain.applicationservice;

import br.com.sistemaos.domain.entity.*;
import br.com.sistemaos.domain.exception.OsNaoEncontradaException;
import br.com.sistemaos.domain.model.Status;
import br.com.sistemaos.domain.model.StatusOs;
import br.com.sistemaos.domain.repository.ClienteRepository;
import br.com.sistemaos.domain.repository.OsRepository;
import br.com.sistemaos.domain.repository.UsuarioRepository;
import br.com.sistemaos.infraestrura.dto.*;
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
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class OsService {
    private final OsRepository osRepository;
    private final ClienteService clienteService;
    private final UsuarioService usuarioService;
    private final PecaService pecaService;

    @Transactional
    public Os adicionarOs(SalvarOsDTO salvarOsDTO) {
        Cliente cliente = clienteService.carregarCliente(salvarOsDTO.getClienteId());
        Usuario usuario = usuarioService.carregarUsuario(salvarOsDTO.getUsuarioId());
        Orcamento orcamento = montarOrcamento(salvarOsDTO.getOrcamento());

        Os os = Os.builder()
                .dataEmissao(LocalDate.now())
                .situacao(StatusOs.PENDENTE)
                .cliente(cliente)
                .usuario(usuario)
                .orcamento(orcamento)
                .status(Status.ATIVO)
                .modelo(salvarOsDTO.getModelo())
                .cor(salvarOsDTO.getCor())
                .build();
        osRepository.save(os);

        return os;
    }

    public Map<String, Object> listarOs(Long id, String nome, List<Status> status, Pageable pageable) {
        Page<OsListagemDTO> listaOs;

        listaOs = osRepository.listarOs(id, nome, status, pageable);
        List<OsListagemDTO> valor = listaOs.getContent();

        Map<String, Object> resposta = new HashMap<>();

        resposta.put("listaOs", valor);
        resposta.put("total", listaOs.getTotalElements());

        return resposta;
    }

    @Transactional
    public Os atualizarStatus(Long id) {
        Os os = carregarOs(id);
        os.setStatus(trocarStatus(os));
        return os;
    }

    public Os carregarOs(Long id) {
        return osRepository.findById(id)
                .orElseThrow(() -> new OsNaoEncontradaException(id));
    }

    private Orcamento montarOrcamento(SalvarOrcamentoDTO dto) {
        List<ItemOrcamento> itens = dto.getItens().stream()
                .map(this::montarItem)
                .toList();

        double valorPecas = itens.stream().mapToDouble(ItemOrcamento::getValorTotal).sum();
        double valorServico = Optional.ofNullable(dto.getValorServico()).orElse(0.0);

        Orcamento orcamento = new Orcamento();
        orcamento.setValorServico(valorServico);
        orcamento.setObservacoes(dto.getObservacoes());
        orcamento.setValorTotal(valorPecas + valorServico);
        orcamento.setItemOrcamento(itens);

        itens.forEach(item -> item.setOrcamento(orcamento));

        return orcamento;
    }

    private ItemOrcamento montarItem(SalvarItemOrcamentoDTO dto) {
        Peca peca = pecaService.carregarPeca(dto.getPecaId());

        ItemOrcamento item = new ItemOrcamento();
        item.setQuantidade(dto.getQuantidade());
        item.setValorUnitario(peca.getPreco());
        item.setValorTotal(peca.getPreco() * dto.getQuantidade());
        item.setItem(peca);

        return item;
    }

    private Status trocarStatus(Os os) {
        if (os.getStatus().equals(Status.ATIVO)) {
            return Status.INATIVO;
        } else {
            return  Status.ATIVO;
        }
    }
}