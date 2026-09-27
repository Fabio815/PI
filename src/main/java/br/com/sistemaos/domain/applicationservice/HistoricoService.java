package br.com.sistemaos.domain.applicationservice;

import br.com.sistemaos.domain.entity.*;
import br.com.sistemaos.domain.model.Status;
import br.com.sistemaos.domain.repository.HistoricoOsRepository;
import br.com.sistemaos.infraestrura.dto.HistoricoOsDTO;
import br.com.sistemaos.infraestrura.dto.SalvarOrcamentoDTO;
import br.com.sistemaos.infraestrura.dto.SalvarOsDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Responsável por comparar o estado antigo x novo da OS e montar o histórico
 * de alterações (HistoricoOs). Só monta a lista em memória - quem persiste
 * é o OsService, depois de garantir que a OS foi salva com sucesso.
 */
@Service
@RequiredArgsConstructor
public class HistoricoService {

    private final HistoricoOsRepository historicoOsRepository;

    public List<HistoricoOs> compararEGerarHistorico(Os osAntiga, SalvarOsDTO dto, Cliente novoCliente, Usuario usuarioLogado) {
        List<HistoricoOs> historicos = new ArrayList<>();

        adicionarSeDiferente(historicos, "Modelo", osAntiga.getModelo(), dto.getModelo(), usuarioLogado);
        adicionarSeDiferente(historicos, "Cor", osAntiga.getCor(), dto.getCor(), usuarioLogado);

        adicionarSeDiferente(historicos, "Situação",
                nomeOuNull(osAntiga.getSituacao()),
                nomeOuNull(dto.getSituacao()),
                usuarioLogado);

        String clienteAntigo = osAntiga.getCliente() == null ? null : osAntiga.getCliente().getNome();
        String clienteNovo = novoCliente == null ? null : novoCliente.getNome();
        adicionarSeDiferente(historicos, "Cliente", clienteAntigo, clienteNovo, usuarioLogado);

        historicos.addAll(compararOrcamento(osAntiga.getOrcamento(), dto.getOrcamento(), usuarioLogado));

        return historicos;
    }

    public List<HistoricoOs> gerarHistoricoStatus(Status statusAntigo, Status statusNovo, Usuario usuarioLogado) {
        List<HistoricoOs> historicos = new ArrayList<>();
        adicionarSeDiferente(historicos, "Status", nomeOuNull(statusAntigo), nomeOuNull(statusNovo), usuarioLogado);
        return historicos;
    }

    private List<HistoricoOs> compararOrcamento(Orcamento orcamentoAntigo, SalvarOrcamentoDTO orcamentoNovo, Usuario usuarioLogado) {
        List<HistoricoOs> historicos = new ArrayList<>();

        if (orcamentoAntigo == null || orcamentoNovo == null) {
            return historicos;
        }

        adicionarSeDiferente(historicos, "Valor do serviço",
                valorParaString(orcamentoAntigo.getValorServico()),
                valorParaString(orcamentoNovo.getValorServico()),
                usuarioLogado);

        adicionarSeDiferente(historicos, "Observações do orçamento",
                orcamentoAntigo.getObservacoes(),
                orcamentoNovo.getObservacoes(),
                usuarioLogado);

        return historicos;
    }

    private void adicionarSeDiferente(List<HistoricoOs> lista, String campo,
                                      String valorAntigo, String valorNovo, Usuario usuario) {
        if (!Objects.equals(valorAntigo, valorNovo)) {
            lista.add(HistoricoOs.builder()
                    .dataAlteracao(LocalDateTime.now())
                    .campoAlterado(campo)
                    .de(valorAntigo)
                    .para(valorNovo)
                    .usuario(usuario)
                    .build());
        }
    }

    private String nomeOuNull(Enum<?> valor) {
        return valor == null ? null : valor.name();
    }

    private String valorParaString(Double valor) {
        return valor == null ? null : String.valueOf(valor);
    }

    /**
     * Vincula cada histórico à OS já salva e persiste tudo de uma vez.
     * Não faz nada se a lista estiver vazia (nenhum campo mudou).
     */
    public void salvarTodos(List<HistoricoOs> historicos, Os os) {
        if (historicos.isEmpty()) {
            return;
        }
        historicos.forEach(h -> h.setOs(os));
        historicoOsRepository.saveAll(historicos);
    }

    public List<HistoricoOsDTO> listarPorOs(Long osId) {
        return historicoOsRepository.findByOsIdOrderByDataAlteracaoDesc(osId)
                .stream()
                .map(HistoricoOsDTO::criar)
                .toList();
    }
}