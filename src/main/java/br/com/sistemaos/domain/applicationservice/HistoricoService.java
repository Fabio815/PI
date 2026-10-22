package br.com.sistemaos.domain.applicationservice;

import br.com.sistemaos.domain.entity.*;
import br.com.sistemaos.domain.model.Status;
import br.com.sistemaos.domain.model.TipoEventoHistorico;
import br.com.sistemaos.domain.repository.HistoricoOsRepository;
import br.com.sistemaos.infraestrura.dto.HistoricoOsDTO;
import br.com.sistemaos.infraestrura.dto.SalvarOsDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HistoricoService {
    private final HistoricoOsRepository historicoOsRepository;

    public List<HistoricoOs> compararEGerarHistorico(Os antiga, SalvarOsDTO dto, Cliente novoCliente,
                                                      Usuario ator, Orcamento proposta) {
        List<HistoricoOs> eventos = new ArrayList<>();
        campo(eventos, "Modelo", antiga.getModelo(), dto.getModelo(), ator);
        campo(eventos, "Cor", antiga.getCor(), dto.getCor(), ator);
        Long clienteAntigoId = antiga.getCliente() == null ? null : antiga.getCliente().getId();
        Long clienteNovoId = novoCliente == null ? null : novoCliente.getId();
        if (!Objects.equals(clienteAntigoId, clienteNovoId)) {
            campo(eventos, "Cliente", rotuloCliente(antiga.getCliente()), rotuloCliente(novoCliente), ator);
        }
        compararOrcamento(eventos, antiga.getOrcamento(), proposta, ator);
        return eventos;
    }

    private void compararOrcamento(List<HistoricoOs> eventos, Orcamento antigo, Orcamento novo, Usuario ator) {
        if (antigo == null || novo == null) return;
        campo(eventos, "Mão de obra", dinheiro(antigo.getValorServico()), dinheiro(novo.getValorServico()), ator);
        campo(eventos, "Observações", antigo.getObservacoes(), novo.getObservacoes(), ator);
        Map<Long, SnapshotItem> anteriores = itens(antigo);
        Map<Long, SnapshotItem> atuais = itens(novo);
        TreeSet<Long> ids = new TreeSet<>();
        ids.addAll(anteriores.keySet());
        ids.addAll(atuais.keySet());
        for (Long id : ids) {
            SnapshotItem a = anteriores.get(id), n = atuais.get(id);
            if (!Objects.equals(a, n)) {
                String nome = n != null ? n.nome() : a.nome();
                campo(eventos, limitar("Item " + id + " " + nome), descrever(a), descrever(n), ator);
            }
        }
    }

    private Map<Long, SnapshotItem> itens(Orcamento o) {
        if (o.getItemOrcamento() == null) return Map.of();
        Map<Long, List<ItemOrcamento>> agrupados = o.getItemOrcamento().stream()
                .collect(Collectors.groupingBy(i -> i.getItem() == null ? -1L : i.getItem().getId(), TreeMap::new, Collectors.toList()));
        Map<Long, SnapshotItem> resultado = new TreeMap<>();
        agrupados.forEach((id, lista) -> {
            int quantidade = lista.stream().mapToInt(ItemOrcamento::getQuantidade).sum();
            BigDecimal total = lista.stream().map(i -> BigDecimal.valueOf(i.getValorTotal() == null ? 0 : i.getValorTotal()))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            Double unitario = lista.isEmpty() ? null : lista.get(0).getValorUnitario();
            String nome = lista.isEmpty() || lista.get(0).getItem() == null ? "Peça" : lista.get(0).getItem().getNome();
            resultado.put(id, new SnapshotItem(nome, quantidade, unitario, total.doubleValue()));
        });
        return resultado;
    }

    private String descrever(SnapshotItem item) {
        if (item == null) return null;
        return String.format(Locale.ROOT, "%s; quantidade=%d; preçoUnitário=%s; total=%s",
                item.nome(), item.quantidade(), dinheiro(item.preco()), dinheiro(item.total()));
    }

    public List<HistoricoOs> gerarHistoricoStatus(Status antigo, Status novo, Usuario ator) {
        List<HistoricoOs> eventos = new ArrayList<>();
        adicionar(eventos, "Status", nome(antigo), nome(novo), ator,
                TipoEventoHistorico.ALTERACAO_STATUS, "Status ativo/inativo alterado");
        return eventos;
    }

    public static HistoricoOs novoEvento(Os os, Usuario ator, TipoEventoHistorico tipo, String campo,
                                          String de, String para, String descricao,
                                          Usuario responsavelAnterior, Usuario novoResponsavel) {
        return HistoricoOs.builder().os(os).usuario(ator).tipoEvento(tipo).campoAlterado(limitar(campo))
                .de(de).para(para).descricao(descricao).dataAlteracao(LocalDateTime.now())
                .responsavelAnterior(responsavelAnterior).novoResponsavel(novoResponsavel).build();
    }

    private void campo(List<HistoricoOs> eventos, String campo, String de, String para, Usuario ator) {
        adicionar(eventos, campo, de, para, ator, TipoEventoHistorico.EDICAO_CAMPOS,
                "Campo alterado: " + campo);
    }

    private void adicionar(List<HistoricoOs> eventos, String campo, String de, String para,
                           Usuario ator, TipoEventoHistorico tipo, String descricao) {
        if (!Objects.equals(de, para)) {
            eventos.add(novoEvento(null, ator, tipo, campo, de, para, descricao, null, null));
        }
    }

    public void salvarTodos(List<HistoricoOs> eventos, Os os) {
        if (eventos == null || eventos.isEmpty()) return;
        eventos.forEach(e -> e.setOs(os));
        historicoOsRepository.saveAll(eventos);
    }

    public List<HistoricoOs> registrosPorOs(Long osId) {
        return historicoOsRepository.findByOsIdOrderByDataAlteracaoDescIdDesc(osId);
    }

    /** Compatibilidade com a consulta de histórico anterior à adição de filtros. */
    public List<HistoricoOsDTO> listarPorOs(Long osId) {
        return registrosPorOs(osId).stream().map(this::dto).toList();
    }

    /** Infere apenas o filtro/DTO em memória: nenhuma linha legada é atualizada ou inventada. */
    public TipoEventoHistorico tipoEfetivo(HistoricoOs h) {
        if (h.getTipoEvento() != null) return h.getTipoEvento();
        String campo = h.getCampoAlterado() == null ? "" : h.getCampoAlterado().toLowerCase(Locale.ROOT);
        if (campo.equals("status") || campo.equals("ativo") || campo.equals("situação") || campo.equals("situacao"))
            return TipoEventoHistorico.ALTERACAO_STATUS;
        if (campo.contains("responsável") || campo.contains("responsavel"))
            return TipoEventoHistorico.REATRIBUICAO;
        return TipoEventoHistorico.EDICAO_CAMPOS;
    }

    public String descricaoEfetiva(HistoricoOs h) {
        return h.getDescricao() != null ? h.getDescricao() : "Alteração legada: " + h.getCampoAlterado();
    }

    public HistoricoOsDTO dto(HistoricoOs h) {
        return HistoricoOsDTO.criar(h, tipoEfetivo(h), descricaoEfetiva(h));
    }

    private String rotuloCliente(Cliente cliente) {
        return cliente == null ? null : cliente.getId() + " - " + cliente.getNome();
    }

    private String dinheiro(Double valor) {
        return valor == null ? null : BigDecimal.valueOf(valor).setScale(2, java.math.RoundingMode.HALF_UP).toPlainString();
    }

    private String nome(Enum<?> valor) { return valor == null ? null : valor.name(); }
    private static String limitar(String valor) { return valor != null && valor.length() > 50 ? valor.substring(0, 50) : valor; }
    private record SnapshotItem(String nome, int quantidade, Double preco, Double total) { }
}
