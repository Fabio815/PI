package br.com.sistemaos.domain.applicationservice;

import br.com.sistemaos.domain.entity.Cliente;
import br.com.sistemaos.domain.entity.Orcamento;
import br.com.sistemaos.domain.entity.Os;
import br.com.sistemaos.domain.entity.Usuario;
import br.com.sistemaos.domain.model.Status;
import br.com.sistemaos.domain.model.StatusOs;
import br.com.sistemaos.infraestrura.dto.RelatorioOsLinhaDTO;
import br.com.sistemaos.infraestrura.dto.RelatorioOsPorPeriodoDTO;
import br.com.sistemaos.infraestrura.dto.RelatorioOsPorStatusDTO;
import br.com.sistemaos.infraestrura.dto.RelatorioOsRespostaDTO;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.Tuple;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Service
public class OsRelatorioService {
    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Executa a página e os agrupamentos na mesma transação read-only repeatable-read,
     * para que linhas, total e gráficos observem o mesmo snapshot de dados.
     */
    @Transactional(readOnly = true, isolation = Isolation.REPEATABLE_READ)
    public RelatorioOsRespostaDTO consultar(LocalDate dataInicio,
                                             LocalDate dataFim,
                                             Long clienteId,
                                             StatusOs situacao,
                                             Status status,
                                             Long responsavelId,
                                             int start,
                                             int limit) {
        validarParametros(dataInicio, dataFim, start, limit);
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        CriteriaQuery<Tuple> paginaQuery = cb.createTupleQuery();
        Root<Os> os = paginaQuery.from(Os.class);
        Join<Os, Cliente> cliente = os.join("cliente", JoinType.LEFT);
        Join<Os, Usuario> responsavel = os.join("responsavel", JoinType.LEFT);
        Join<Os, Usuario> criador = os.join("usuario", JoinType.LEFT);
        Join<Os, Orcamento> orcamento = os.join("orcamento", JoinType.LEFT);
        Expression<Double> valorTotal = cb.coalesce(orcamento.<Double>get("valorTotal"), 0.0d);
        paginaQuery.multiselect(
                os.<Long>get("id").alias("id"),
                os.<LocalDate>get("dataEmissao").alias("dataEmissao"),
                os.<LocalDate>get("dataFim").alias("dataFim"),
                cliente.<Long>get("id").alias("clienteId"),
                cliente.<String>get("nome").alias("clienteNome"),
                cb.coalesce(responsavel.<Long>get("id"), criador.<Long>get("id")).alias("responsavelId"),
                cb.coalesce(responsavel.<String>get("nome"), criador.<String>get("nome")).alias("responsavelNome"),
                os.<StatusOs>get("situacao").alias("situacao"),
                os.<Status>get("status").alias("status"),
                valorTotal.alias("valorTotal")
        );
        paginaQuery.where(predicados(cb, os, dataInicio, dataFim, clienteId, situacao, status, responsavelId));
        paginaQuery.orderBy(cb.desc(os.get("dataEmissao")), cb.desc(os.get("id")));
        List<Tuple> resultados = entityManager.createQuery(paginaQuery)
                .setFirstResult(start)
                .setMaxResults(limit)
                .getResultList();
        List<RelatorioOsLinhaDTO> linhas = resultados.stream().map(t -> new RelatorioOsLinhaDTO(
                t.get("id", Long.class),
                t.get("dataEmissao", LocalDate.class),
                t.get("dataFim", LocalDate.class),
                t.get("clienteId", Long.class),
                t.get("clienteNome", String.class),
                t.get("responsavelId", Long.class),
                t.get("responsavelNome", String.class),
                t.get("situacao", StatusOs.class),
                t.get("status", Status.class),
                t.get("valorTotal", Double.class)
        )).toList();

        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<Os> countRoot = countQuery.from(Os.class);
        countQuery.select(cb.count(countRoot));
        countQuery.where(predicados(cb, countRoot, dataInicio, dataFim, clienteId, situacao, status, responsavelId));
        long total = entityManager.createQuery(countQuery).getSingleResult();

        // Uma consulta agrupada por data + situação substitui a carga de entidades e
        // permite derivar os dois gráficos sobre o mesmo conjunto completo de filtros.
        CriteriaQuery<Tuple> aggregateQuery = cb.createTupleQuery();
        Root<Os> aggregateRoot = aggregateQuery.from(Os.class);
        Path<StatusOs> situacaoPath = aggregateRoot.get("situacao");
        Path<LocalDate> dataPath = aggregateRoot.get("dataEmissao");
        aggregateQuery.multiselect(situacaoPath.alias("situacao"), dataPath.alias("periodo"), cb.count(aggregateRoot).alias("total"));
        aggregateQuery.where(predicados(cb, aggregateRoot, dataInicio, dataFim, clienteId, situacao, status, responsavelId));
        aggregateQuery.groupBy(situacaoPath, dataPath);
        List<Tuple> grupos = entityManager.createQuery(aggregateQuery).getResultList();

        Map<StatusOs, Long> statusTotals = new EnumMap<>(StatusOs.class);
        for (StatusOs valor : StatusOs.values()) {
            statusTotals.put(valor, 0L);
        }
        Map<LocalDate, Long> periodTotals = new TreeMap<>();
        for (Tuple grupo : grupos) {
            StatusOs grupoSituacao = grupo.get("situacao", StatusOs.class);
            LocalDate periodo = grupo.get("periodo", LocalDate.class);
            Long quantidade = grupo.get("total", Long.class);
            if (grupoSituacao != null) {
                statusTotals.merge(grupoSituacao, quantidade, Long::sum);
            }
            if (periodo != null) {
                periodTotals.merge(periodo, quantidade, Long::sum);
            }
        }
        List<RelatorioOsPorStatusDTO> porStatus = new ArrayList<>();
        for (StatusOs valor : StatusOs.values()) {
            porStatus.add(new RelatorioOsPorStatusDTO(valor, statusTotals.get(valor)));
        }
        List<RelatorioOsPorPeriodoDTO> porPeriodo = periodTotals.entrySet().stream()
                .map(entry -> new RelatorioOsPorPeriodoDTO(entry.getKey().toString(), entry.getValue()))
                .toList();

        Map<String, Object> filtros = new LinkedHashMap<>();
        filtros.put("dataInicio", dataInicio);
        filtros.put("dataFim", dataFim);
        filtros.put("clienteId", clienteId);
        filtros.put("situacao", situacao);
        filtros.put("status", status);
        filtros.put("responsavelId", responsavelId);
        filtros.put("start", start);
        filtros.put("limit", limit);
        return new RelatorioOsRespostaDTO(linhas, total, porStatus, porPeriodo, filtros);
    }

    private List<Predicate> predicados(CriteriaBuilder cb,
                                       Root<Os> os,
                                       LocalDate dataInicio,
                                       LocalDate dataFim,
                                       Long clienteId,
                                       StatusOs situacao,
                                       Status status,
                                       Long responsavelId) {
        List<Predicate> conditions = new ArrayList<>();
        if (dataInicio != null) {
            conditions.add(cb.greaterThanOrEqualTo(os.<LocalDate>get("dataEmissao"), dataInicio));
        }
        if (dataFim != null) {
            conditions.add(cb.lessThanOrEqualTo(os.<LocalDate>get("dataEmissao"), dataFim));
        }
        if (clienteId != null) {
            conditions.add(cb.equal(os.get("cliente").get("id"), clienteId));
        }
        if (situacao != null) {
            conditions.add(cb.equal(os.get("situacao"), situacao));
        }
        if (status != null) {
            conditions.add(cb.equal(os.get("status"), status));
        }
        if (responsavelId != null) {
            Join<Os, Usuario> responsavel = os.join("responsavel", JoinType.LEFT);
            Join<Os, Usuario> criador = os.join("usuario", JoinType.LEFT);
            conditions.add(cb.equal(cb.coalesce(responsavel.<Long>get("id"), criador.<Long>get("id")), responsavelId));
        }
        return conditions;
    }

    private void validarParametros(LocalDate dataInicio, LocalDate dataFim, int start, int limit) {
        if (dataInicio != null && dataFim != null && dataInicio.isAfter(dataFim)) {
            throw new IllegalArgumentException("dataInicio não pode ser posterior a dataFim.");
        }
        if (start < 0 || start > 1_000_000) {
            throw new IllegalArgumentException("start deve estar entre 0 e 1000000.");
        }
        if (limit < 1 || limit > 100) {
            throw new IllegalArgumentException("limit deve estar entre 1 e 100.");
        }
    }
}
