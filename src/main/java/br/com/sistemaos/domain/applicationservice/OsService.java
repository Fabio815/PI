package br.com.sistemaos.domain.applicationservice;

import br.com.sistemaos.domain.entity.*;
import br.com.sistemaos.domain.exception.AcessoNegadoException;
import br.com.sistemaos.domain.exception.OsNaoEncontradaException;
import br.com.sistemaos.domain.model.Perfil;
import br.com.sistemaos.domain.model.Status;
import br.com.sistemaos.domain.model.StatusOs;
import br.com.sistemaos.domain.repository.OsRepository;
import br.com.sistemaos.infraestrura.dto.*;
import br.com.sistemaos.infraestrura.security.UsuarioLogadoService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
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
    private final PecaService pecaService;
    private final UsuarioLogadoService usuarioLogadoService;

    @Transactional
    public ResultadoCadastroOs adicionarOs(SalvarOsDTO salvarOsDTO) {
        Cliente cliente = clienteService.carregarCliente(salvarOsDTO.getClienteId());
        Usuario usuario = usuarioLogadoService.obterUsuarioLogado();
        List<String> avisosEstoque = new ArrayList<>();
        Orcamento orcamento = montarOrcamento(salvarOsDTO.getOrcamento(), true, avisosEstoque);

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

        return new ResultadoCadastroOs(os, List.copyOf(avisosEstoque));
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
        validarPermissaoEdicao(os);
        os.setStatus(trocarStatus(os));
        return os;
    }

    @Transactional
    public Os atualizarOs(Long id, SalvarOsDTO salvarOsDTO) {
        Os os = carregarOs(id);
        validarPermissaoEdicao(os);

        Cliente cliente = clienteService.carregarCliente(salvarOsDTO.getClienteId());
        Orcamento orcamento = montarOrcamento(salvarOsDTO.getOrcamento());

        os.setModelo(salvarOsDTO.getModelo());
        os.setCor(salvarOsDTO.getCor());
        os.setSituacao(salvarOsDTO.getSituacao());
        os.setCliente(cliente);
        os.setOrcamento(orcamento);

        return os;
    }

    public Os carregarOs(Long id) {
        return osRepository.findById(id)
                .orElseThrow(() -> new OsNaoEncontradaException(id));
    }

    /**
     * ADM pode alterar qualquer OS. FUNCIONARIO só pode alterar a OS que ele mesmo criou
     * (visualizar/listar continua liberado para qualquer um).
     */
    private void validarPermissaoEdicao(Os os) {
        Usuario usuarioLogado = usuarioLogadoService.obterUsuarioLogado();

        if (usuarioLogado.getChave() == Perfil.ADM) {
            return;
        }

        if (os.getUsuario() == null || !os.getUsuario().getId().equals(usuarioLogado.getId())) {
            throw new AcessoNegadoException("Você só pode alterar as ordens de serviço que você criou");
        }
    }

    private Orcamento montarOrcamento(SalvarOrcamentoDTO dto) {
        return montarOrcamento(dto, false, new ArrayList<>());
    }

    private Orcamento montarOrcamento(
            SalvarOrcamentoDTO dto,
            boolean baixarEstoque,
            List<String> avisosEstoque) {
        List<ItemOrcamento> itens = dto.getItens().stream()
                .map(itemDto -> montarItem(itemDto, baixarEstoque, avisosEstoque))
                .toList();

        double valorPecas = arredondarMoeda(
                itens.stream().mapToDouble(ItemOrcamento::getValorTotal).sum());
        double valorServico = arredondarMoeda(
                Optional.ofNullable(dto.getValorServico()).orElse(0.0));

        Orcamento orcamento = new Orcamento();
        orcamento.setValorServico(valorServico);
        orcamento.setObservacoes(dto.getObservacoes());
        orcamento.setValorTotal(arredondarMoeda(valorPecas + valorServico));
        orcamento.setItemOrcamento(itens);

        itens.forEach(item -> item.setOrcamento(orcamento));

        return orcamento;
    }

    private ItemOrcamento montarItem(
            SalvarItemOrcamentoDTO dto,
            boolean baixarEstoque,
            List<String> avisosEstoque) {
        Peca peca = baixarEstoque
                ? pecaService.carregarPecaParaBaixa(dto.getPecaId())
                : pecaService.carregarPeca(dto.getPecaId());

        int quantidadeSolicitada = dto.getQuantidade();

        if (baixarEstoque) {
            int quantidadeDisponivel = peca.getQuantidade();

            if (quantidadeDisponivel < quantidadeSolicitada) {
                int quantidadeFaltante = quantidadeSolicitada - quantidadeDisponivel;
                avisosEstoque.add(String.format(
                        "Peça '%s': solicitada %d, disponível %d, faltam %d unidade(s).",
                        peca.getNome(),
                        quantidadeSolicitada,
                        quantidadeDisponivel,
                        quantidadeFaltante));
            }

            // A OS não é bloqueada por insuficiência. A baixa integral registra o déficit
            // como estoque negativo, preservando exatamente a quantidade usada na OS.
            peca.setQuantidade(quantidadeDisponivel - quantidadeSolicitada);
        }

        double valorUnitario = arredondarMoeda(peca.getPreco());

        ItemOrcamento item = new ItemOrcamento();
        item.setQuantidade(quantidadeSolicitada);
        item.setValorUnitario(valorUnitario);
        item.setValorTotal(arredondarMoeda(valorUnitario * quantidadeSolicitada));
        item.setItem(peca);

        return item;
    }

    private double arredondarMoeda(Double valor) {
        return BigDecimal.valueOf(valor)
                .setScale(2, RoundingMode.HALF_UP)
                .doubleValue();
    }

    private Status trocarStatus(Os os) {
        if (os.getStatus().equals(Status.ATIVO)) {
            return Status.INATIVO;
        } else {
            return  Status.ATIVO;
        }
    }

    public record ResultadoCadastroOs(Os os, List<String> avisosEstoque) {
    }
}