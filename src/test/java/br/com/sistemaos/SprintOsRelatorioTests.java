package br.com.sistemaos;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class SprintOsRelatorioTests extends SprintOsTestSupport {
    private void massaRelatorio() throws Exception {
        long a = os(autor, autorId, 1);
        long b = os(autor, outroId, 1);
        long c = os(admin, autorId, 1);
        long d = os(admin, outroId, 1);
        jdbc.update("update os set data_emissao='2026-10-01', situacao='PENDENTE' where id=?", a);
        jdbc.update("update os set data_emissao='2026-10-02', situacao='CONCLUIDO', data_fim='2026-10-03' where id=?", b);
        jdbc.update("update os set data_emissao='2026-10-02', situacao='PENDENTE', cliente_id=? where id=?", outroClienteId, c);
        jdbc.update("update os set data_emissao='2026-09-30', situacao='CANCELADO', status='INATIVO', data_fim='2026-10-01' where id=?", d);
    }

    private String relatorio(String filtros) throws Exception {
        return body(request("GET", "/os/relatorio" + filtros, autor, null, 200));
    }

    @Test
    void graficosUsamTodosResultadosFiltradosNaoApenasPagina() throws Exception {
        massaRelatorio();
        String resposta = relatorio("?start=0&limit=2");
        assertEquals(4, numero(resposta, "$.total"));
        List<?> linhas = JsonPath.read(resposta, "$.linhas");
        assertEquals(2, linhas.size());
        assertEquals(4, somar(resposta, "$.porStatus"));
        assertEquals(4, somar(resposta, "$.porPeriodo"));
        String pagina2 = relatorio("?start=2&limit=2");
        assertEquals(2, ((List<?>) JsonPath.read(pagina2, "$.linhas")).size());
        assertEquals(JsonPath.<Object>read(resposta, "$.porStatus"), JsonPath.<Object>read(pagina2, "$.porStatus"));
        assertEquals(JsonPath.<Object>read(resposta, "$.porPeriodo"), JsonPath.<Object>read(pagina2, "$.porPeriodo"));
    }

    @Test
    void filtrosDataClienteSituacaoStatusResponsavelCombinamPorAnd() throws Exception {
        massaRelatorio();
        String resposta = relatorio("?dataInicio=2026-10-01&dataFim=2026-10-02&clienteId=" + clienteId
                + "&situacao=PENDENTE&status=ATIVO&responsavelId=" + autorId);
        assertEquals(1, numero(resposta, "$.total"));
        assertEquals(1, somar(resposta, "$.porStatus"));
        assertEquals(1, somar(resposta, "$.porPeriodo"));
        assertEquals(clienteId.longValue(), numero(resposta, "$.linhas[0].clienteId"));
        assertEquals(autorId.longValue(), numero(resposta, "$.linhas[0].responsavelId"));
        assertEquals("2026-10-01", JsonPath.read(resposta, "$.linhas[0].dataEmissao"));
        assertEquals("PENDENTE", JsonPath.read(resposta, "$.linhas[0].situacao"));
    }

    @Test
    void periodoIncluiAsDuasDatasDeBorda() throws Exception {
        massaRelatorio();
        String resposta = relatorio("?dataInicio=2026-10-01&dataFim=2026-10-02");
        assertEquals(3, numero(resposta, "$.total"));
        assertEquals(3, somar(resposta, "$.porPeriodo"));
        List<Map<String, Object>> periodos = JsonPath.read(resposta, "$.porPeriodo");
        assertTrue(periodos.stream().anyMatch(p -> "2026-10-01".equals(p.get("periodo"))));
        assertTrue(periodos.stream().anyMatch(p -> "2026-10-02".equals(p.get("periodo"))));
    }

    @Test
    void filtrosSemResultadosDevolvemTabelaEGraficosSemDados() throws Exception {
        massaRelatorio();
        String resposta = relatorio("?dataInicio=2000-01-01&dataFim=2000-01-02");
        assertEquals(0, numero(resposta, "$.total"));
        assertTrue(((List<?>) JsonPath.read(resposta, "$.linhas")).isEmpty());
        assertEquals(0, somar(resposta, "$.porStatus"));
        assertEquals(0, somar(resposta, "$.porPeriodo"));
    }

    @Test
    void paginaForaDoFimMantemTotalEAgregadosDoConjunto() throws Exception {
        massaRelatorio();
        String resposta = relatorio("?start=100&limit=2");
        assertEquals(4, numero(resposta, "$.total"));
        assertTrue(((List<?>) JsonPath.read(resposta, "$.linhas")).isEmpty());
        assertEquals(4, somar(resposta, "$.porStatus"));
    }

    @Test
    void periodoOuPaginacaoInvalidosRetornam400() throws Exception {
        request("GET", "/os/relatorio?dataInicio=2026-10-20&dataFim=2026-10-01", autor, null, 400);
        request("GET", "/os/relatorio?limit=0", autor, null, 400);
        request("GET", "/os/relatorio?start=-1", autor, null, 400);
        request("GET", "/os/relatorio?dataInicio=banana", autor, null, 400);
        request("GET", "/os/relatorio?situacao=INVALIDA", autor, null, 400);
    }

    @Test
    void filtrosNaoExigemClienteOuDatasParaListarTodos() throws Exception {
        massaRelatorio();
        assertEquals(4, numero(relatorio(""), "$.total"));
        assertEquals(1, numero(relatorio("?status=INATIVO"), "$.total"));
        assertEquals(2, numero(relatorio("?responsavelId=" + outroId), "$.total"));
    }

    @Test
    void respostaRelatorioNaoExpoeDadosDeAutenticacao() throws Exception {
        massaRelatorio();
        String resposta = relatorio("");
        assertFalse(resposta.contains("senha"));
        assertFalse(resposta.contains("resetToken"));
        assertTrue(resposta.contains("Cliente Alfa"));
    }

    private long somar(String resposta, String path) {
        List<Map<String, Object>> pontos = JsonPath.read(resposta, path);
        return pontos.stream().mapToLong(p -> ((Number) p.get("total")).longValue()).sum();
    }
}
