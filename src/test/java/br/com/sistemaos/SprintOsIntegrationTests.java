package br.com.sistemaos;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.*;

class SprintOsIntegrationTests extends SprintOsTestSupport {
    @Test
    void criacaoPorComumDistingueCriadorResponsavelEBaixaEstoque() throws Exception {
        long id = os(autor, outroId, 3);
        String resposta = body(request("GET", "/os/" + id, autor, null, 200));
        assertEquals(autorId.longValue(), numero(resposta, "$.usuario.id"));
        assertEquals(outroId.longValue(), numero(resposta, "$.responsavel.id"));
        assertEquals(3, numero(resposta, "$.orcamento.itens[0].quantidade"));
        assertEquals(17, saldo());
        assertEquals(1, quantidadeEvento(id, "CRIACAO"));
        assertEquals(1, quantidadeEvento(id, "ATRIBUICAO"));
        for (Map<String, Object> evento : historico(id)) {
            assertNotNull(evento.get("tipoEvento"));
            assertNotNull(evento.get("descricao"));
            assertNotNull(evento.get("dataAlteracao"));
            Map<String, Object> usuario = (Map<String, Object>) evento.get("usuario");
            assertEquals(autorId.longValue(), ((Number) usuario.get("id")).longValue());
        }
    }

    @Test
    void responsavelAusenteNaCriacaoAssumeCriador() throws Exception {
        long id = os(autor, null, 1);
        assertEquals(autorId.longValue(), numero(body(request("GET", "/os/" + id, autor, null, 200)), "$.responsavel.id"));
    }

    @Test
    void administradorPodeCriarOsParaOutroResponsavel() throws Exception {
        long id = os(admin, autorId, 1);
        String resposta = body(request("GET", "/os/" + id, admin, null, 200));
        assertEquals(adminId.longValue(), numero(resposta, "$.usuario.id"));
        assertEquals(autorId.longValue(), numero(resposta, "$.responsavel.id"));
    }

    @Test
    void comumNaoReatribuiNemPeloEndpointNemPelaEdicaoGeral() throws Exception {
        long id = os(autor, autorId, 1);
        request("PUT", "/os/" + id + "/responsavel", autor, Map.of("responsavelId", outroId), 403);
        request("PUT", "/os/atualizar/" + id, autor, payload(outroId, 1), 403);
        assertEquals(autorId.longValue(), numero(body(request("GET", "/os/" + id, autor, null, 200)), "$.responsavel.id"));
        assertEquals(0, quantidadeEvento(id, "REATRIBUICAO"));
    }

    @Test
    void administradorReatribuiQualquerOsComIdentidadesEAtorAuditados() throws Exception {
        long id = os(autor, autorId, 1);
        request("PUT", "/os/" + id + "/responsavel", admin, Map.of("responsavelId", outroId), 200);
        Map<String, Object> evento = historico(id).stream()
                .filter(h -> "REATRIBUICAO".equals(h.get("tipoEvento"))).findFirst().orElseThrow();
        assertEquals(autorId.longValue(), ((Number) ((Map<?, ?>) evento.get("responsavelAnterior")).get("id")).longValue());
        assertEquals(outroId.longValue(), ((Number) ((Map<?, ?>) evento.get("novoResponsavel")).get("id")).longValue());
        assertEquals(adminId.longValue(), ((Number) ((Map<?, ?>) evento.get("usuario")).get("id")).longValue());
        assertEquals(19, saldo());
    }

    @Test
    void reatribuirMesmoResponsavelNaoGeraEventoDuplicado() throws Exception {
        long id = os(autor, autorId, 1);
        int antes = historico(id).size();
        request("PUT", "/os/" + id + "/responsavel", admin, Map.of("responsavelId", autorId), 200);
        assertEquals(antes, historico(id).size());
    }

    @Test
    void responsavelInativoNaoPodeSerDesignado() throws Exception {
        request("POST", "/os/cadastrar", autor, payload(inativoId, 1), 400);
        assertEquals(20, saldo());
        assertEquals(0, jdbc.queryForObject("select count(*) from os", Integer.class));
    }

    @Test
    void responsaveisDisponiveisAComumNaoExibemSenhaOuInativos() throws Exception {
        String resposta = body(request("GET", "/os/responsaveis", autor, null, 200));
        List<Map<String, Object>> usuarios = JsonPath.read(resposta, "$");
        assertEquals(3, usuarios.size());
        assertFalse(resposta.contains("senha"));
        assertFalse(resposta.contains("resetToken"));
        assertFalse(usuarios.stream().anyMatch(u -> inativoId.equals(((Number) u.get("id")).longValue())));
    }

    @Test
    void comumNaoEditaCamposDaOsCriadaPorOutro() throws Exception {
        long id = os(autor, outroId, 1);
        request("PUT", "/os/atualizar/" + id, outro, payload(outroId, 1), 403);
    }

    @Test
    void edicaoRelevanteIncluiModeloEQuantidadeEEmiteAuditoria() throws Exception {
        long id = os(autor, autorId, 1);
        Map<String, Object> dados = payload(null, 4);
        dados.put("modelo", "Modelo atualizado");
        request("PUT", "/os/atualizar/" + id, autor, dados, 200);
        String resposta = body(request("GET", "/os/" + id, autor, null, 200));
        assertEquals("Modelo atualizado", JsonPath.read(resposta, "$.modelo"));
        assertEquals(4, numero(resposta, "$.orcamento.itens[0].quantidade"));
        assertTrue(quantidadeEvento(id, "EDICAO_CAMPOS") >= 1);
        assertTrue(historico(id).stream().anyMatch(h -> String.valueOf(h.get("descricao")).toLowerCase().contains("peça")
                || String.valueOf(h.get("campoAlterado")).toLowerCase().contains("ite")));
        // Nesta sprint a baixa permanece somente na criação.
        assertEquals(19, saldo());
    }

    @Test
    void salvarSemMudancasNaoGeraHistoricoOuOrcamentoExtra() throws Exception {
        long id = os(autor, autorId, 1);
        int antes = historico(id).size();
        int orcamentos = jdbc.queryForObject("select count(*) from orcamento", Integer.class);
        request("PUT", "/os/atualizar/" + id, autor, payload(null, 1), 200);
        assertEquals(antes, historico(id).size());
        assertEquals(orcamentos, jdbc.queryForObject("select count(*) from orcamento", Integer.class));
    }

    @Test
    void alteracaoSituacaoEStatusAtivoGeramEventosPadronizados() throws Exception {
        long id = os(autor, autorId, 1);
        request("PUT", "/os/" + id + "/situacao", autor, Map.of("situacao", "EM_ANDAMENTO"), 200);
        request("PUT", "/os/status/" + id, autor, null, 200);
        assertEquals(2, quantidadeEvento(id, "ALTERACAO_STATUS"));
    }

    @Test
    void concluidaBloqueiaCamposInclusiveAdministradorMasPermiteHistorico() throws Exception {
        long id = os(autor, autorId, 1);
        String resposta = body(request("PUT", "/os/" + id + "/situacao", autor, Map.of("situacao", "CONCLUIDO"), 200));
        assertEquals(Boolean.TRUE, JsonPath.read(resposta, "$.encerrada"));
        assertNotNull(JsonPath.read(resposta, "$.dataFim"));
        request("PUT", "/os/atualizar/" + id, autor, payload(null, 1), 409);
        request("PUT", "/os/atualizar/" + id, admin, payload(null, 1), 409);
        request("PUT", "/os/" + id + "/situacao", admin, Map.of("situacao", "PENDENTE"), 409);
        request("PUT", "/os/status/" + id, autor, null, 409);
        assertEquals(1, quantidadeEvento(id, "CONCLUSAO"));
    }

    @Test
    void canceladaBloqueiaEdicaoMasPermiteComentarioEReatribuicaoAdm() throws Exception {
        long id = os(autor, autorId, 1);
        request("PUT", "/os/" + id + "/situacao", autor, Map.of("situacao", "CANCELADO"), 200);
        request("PUT", "/os/atualizar/" + id, autor, payload(null, 1), 409);
        request("POST", "/os/" + id + "/comentarios", outro, Map.of("conteudo", "Comentário após cancelamento"), 201);
        request("PUT", "/os/" + id + "/responsavel", admin, Map.of("responsavelId", outroId), 200);
        assertEquals(1, quantidadeEvento(id, "CANCELAMENTO"));
        assertEquals(1, quantidadeEvento(id, "REATRIBUICAO"));
    }

    @Test
    void reabrirExigeAdmERetornaPendenteSemNovaBaixa() throws Exception {
        long id = os(autor, autorId, 3);
        request("PUT", "/os/" + id + "/situacao", autor, Map.of("situacao", "CONCLUIDO"), 200);
        request("POST", "/os/" + id + "/reabrir", autor, null, 403);
        String resposta = body(request("POST", "/os/" + id + "/reabrir", admin, null, 200));
        assertEquals("PENDENTE", JsonPath.read(resposta, "$.situacao"));
        assertEquals(Boolean.FALSE, JsonPath.read(resposta, "$.encerrada"));
        assertNull(JsonPath.read(resposta, "$.dataFim"));
        assertEquals(17, saldo());
        assertEquals(1, quantidadeEvento(id, "REABERTURA"));
        request("PUT", "/os/atualizar/" + id, autor, payload(null, 3), 200);
    }

    @Test
    void reabrirOsAbertaNaoPermiteEventoFalso() throws Exception {
        long id = os(autor, autorId, 1);
        request("POST", "/os/" + id + "/reabrir", admin, null, 409);
        assertEquals(0, quantidadeEvento(id, "REABERTURA"));
    }

    @Test
    void comentariosEncerradaPodemSerCriadosEEditadosPeloAutorOuAdm() throws Exception {
        long id = os(autor, autorId, 1);
        request("PUT", "/os/" + id + "/situacao", autor, Map.of("situacao", "CONCLUIDO"), 200);
        String resposta = body(request("POST", "/os/" + id + "/comentarios", outro, Map.of("conteudo", "Texto inicial"), 201));
        long comentario = numero(resposta, "$.id");
        assertEquals(outroId.longValue(), numero(resposta, "$.autor.id"));
        assertNotNull(JsonPath.read(resposta, "$.dataCriacao"));
        request("PUT", "/os/" + id + "/comentarios/" + comentario, autor, Map.of("conteudo", "Alteração indevida"), 403);
        resposta = body(request("PUT", "/os/" + id + "/comentarios/" + comentario, outro, Map.of("conteudo", "Alterado pelo autor"), 200));
        assertEquals(outroId.longValue(), numero(resposta, "$.editadoPor.id"));
        resposta = body(request("PUT", "/os/" + id + "/comentarios/" + comentario, admin, Map.of("conteudo", "Alterado pelo admin"), 200));
        assertEquals(outroId.longValue(), numero(resposta, "$.autor.id"));
        assertEquals(adminId.longValue(), numero(resposta, "$.editadoPor.id"));
        assertNotNull(JsonPath.read(resposta, "$.dataEdicao"));
        assertEquals(1, quantidadeEvento(id, "CRIACAO_COMENTARIO"));
        assertEquals(2, quantidadeEvento(id, "EDICAO_COMENTARIO"));
        assertTrue(historico(id).stream().anyMatch(h -> "Texto inicial".equals(h.get("de"))));
    }

    @Test
    void comentariosNaoPodemSerExcluidos() throws Exception {
        long id = os(autor, autorId, 1);
        long comentario = numero(body(request("POST", "/os/" + id + "/comentarios", autor, Map.of("conteudo", "Preservar"), 201)), "$.id");
        mvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete("/os/" + id + "/comentarios/" + comentario).session(admin))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().is4xxClientError());
        List<?> comentarios = JsonPath.read(body(request("GET", "/os/" + id + "/comentarios", autor, null, 200)), "$");
        assertEquals(1, comentarios.size());
    }

    @Test
    void comentarioNaoPodeSerEditadoComIdDeOutraOs() throws Exception {
        long id1 = os(autor, autorId, 1);
        long id2 = os(autor, autorId, 1);
        long comentario = numero(body(request("POST", "/os/" + id1 + "/comentarios", autor, Map.of("conteudo", "Original"), 201)), "$.id");
        request("PUT", "/os/" + id2 + "/comentarios/" + comentario, admin, Map.of("conteudo", "Intruso"), 404);
        assertEquals(0, quantidadeEvento(id2, "EDICAO_COMENTARIO"));
    }

    @Test
    void comentarioVazioOuExcessivoNaoGeraRegistroNemEvento() throws Exception {
        long id = os(autor, autorId, 1);
        request("POST", "/os/" + id + "/comentarios", autor, Map.of("conteudo", " \n \t "), 400);
        request("POST", "/os/" + id + "/comentarios", autor, Map.of("conteudo", "x".repeat(4001)), 400);
        assertEquals(0, quantidadeEvento(id, "CRIACAO_COMENTARIO"));
    }

    @Test
    void textoLongoDoComentarioPersisteIntegralNaAuditoria() throws Exception {
        long id = os(autor, autorId, 1);
        String texto = "x".repeat(4000);
        request("POST", "/os/" + id + "/comentarios", autor, Map.of("conteudo", texto), 201);
        Map<String, Object> evento = historico(id).stream().filter(h -> "CRIACAO_COMENTARIO".equals(h.get("tipoEvento"))).findFirst().orElseThrow();
        assertEquals(texto, evento.get("para"));
    }

    @Test
    void filtrosHistoricoTipoPeriodoEUsuarioCombinamPorAnd() throws Exception {
        long id = os(autor, autorId, 1);
        request("POST", "/os/" + id + "/comentarios", outro, Map.of("conteudo", "Do outro"), 201);
        request("POST", "/os/" + id + "/comentarios", autor, Map.of("conteudo", "Do autor"), 201);
        String hoje = LocalDate.now().toString();
        String resposta = body(request("GET", "/os/" + id + "/historico?tipoEvento=CRIACAO_COMENTARIO&dataInicio=" + hoje
                + "&dataFim=" + hoje + "&usuarioId=" + outroId, autor, null, 200));
        List<Map<String, Object>> eventos = JsonPath.read(resposta, "$");
        assertEquals(1, eventos.size());
        assertEquals("Do outro", eventos.getFirst().get("para"));
        resposta = body(request("GET", "/os/" + id + "/historico?tipoEvento=CRIACAO_COMENTARIO&dataFim=2000-01-01&usuarioId=" + outroId, autor, null, 200));
        assertTrue(((List<?>) JsonPath.read(resposta, "$")).isEmpty());
    }

    @Test
    void filtrosInvalidosDoHistoricoRetornam400() throws Exception {
        long id = os(autor, autorId, 1);
        request("GET", "/os/" + id + "/historico?dataInicio=2026-10-20&dataFim=2026-10-01", autor, null, 400);
        request("GET", "/os/" + id + "/historico?tipoEvento=INVENTADO", autor, null, 400);
    }

    @Test
    void filtrosLegadosClassificamEventoSemFabricarCriacao() throws Exception {
        long id = os(autor, autorId, 1);
        jdbc.update("insert into historico_os (os_id,usuario_id,data_alteracao,campo_alterado,de,para) values (?,?,CURRENT_TIMESTAMP,?,?,?)",
                id, autorId, "Cor", "Azul", "Vermelho");
        String resposta = body(request("GET", "/os/" + id + "/historico?tipoEvento=EDICAO_CAMPOS", autor, null, 200));
        List<Map<String, Object>> eventos = JsonPath.read(resposta, "$");
        assertEquals(1, eventos.size());
        assertEquals("EDICAO_CAMPOS", eventos.getFirst().get("tipoEvento"));
        assertEquals(1, quantidadeEvento(id, "CRIACAO"));
    }

    @Test
    void faltaEstoqueContinuaInformativaEPersisteQuantidadeIntegral() throws Exception {
        String resposta = body(request("POST", "/os/cadastrar", autor, payload(autorId, 23), 201));
        assertEquals(-3, saldo());
        assertFalse(((List<?>) JsonPath.read(resposta, "$.avisosEstoque")).isEmpty());
        assertEquals(23, numero(resposta, "$.os.orcamento.itens[0].quantidade"));
    }

    @Test
    void falhaEmPecaInexistenteReverteOsHistoricoEBaixaAnterior() throws Exception {
        Map<String, Object> dados = payload(autorId, 1);
        dados.put("orcamento", Map.of("valorServico", 10, "itens", List.of(
                Map.of("pecaId", pecaId, "quantidade", 2, "valorUnitario", 25),
                Map.of("pecaId", 999999, "quantidade", 1, "valorUnitario", 25))));
        request("POST", "/os/cadastrar", autor, dados, 404);
        assertEquals(20, saldo());
        assertEquals(0, jdbc.queryForObject("select count(*) from os", Integer.class));
        assertEquals(0, jdbc.queryForObject("select count(*) from historico_os", Integer.class));
    }

    @Test
    void moedaNegativaEQuantidadeZeroContinuamRejeitadas() throws Exception {
        Map<String, Object> dados = payload(autorId, 0);
        request("POST", "/os/cadastrar", autor, dados, 400);
        dados = payload(autorId, 1);
        dados.put("orcamento", Map.of("valorServico", -0.01, "itens", List.of(Map.of("pecaId", pecaId, "quantidade", 1, "valorUnitario", 25))));
        request("POST", "/os/cadastrar", autor, dados, 400);
        assertEquals(20, saldo());
    }

    @Test
    void duasInsercoesConcorrentesNaoPerdemBaixa() throws Exception {
        try (var executor = Executors.newFixedThreadPool(2)) {
            var results = executor.invokeAll(List.<Callable<Long>>of(
                    () -> os(autor, autorId, 15), () -> os(outro, outroId, 15)));
            for (var result : results) assertTrue(result.get() > 0);
        }
        assertEquals(-10, saldo());
        assertEquals(2, jdbc.queryForObject("select count(*) from os", Integer.class));
    }

    @Test
    void osLegadaSemResponsavelContinuaNaListagemENoRelatorio() throws Exception {
        long id = os(autor, autorId, 1);
        jdbc.update("update os set responsavel_id=null where id=?", id);
        String listagem = body(request("GET", "/os/listar?id=" + id, autor, null, 200));
        assertEquals(1, numero(listagem, "$.total"));
        assertEquals(autorId.longValue(), numero(listagem, "$.listaOs[0].responsavelId"));
        String relatorio = body(request("GET", "/os/relatorio?responsavelId=" + autorId, autor, null, 200));
        assertEquals(1, numero(relatorio, "$.total"));
        assertEquals(autorId.longValue(), numero(relatorio, "$.linhas[0].responsavelId"));
        assertEquals("Autor", JsonPath.read(relatorio, "$.linhas[0].responsavelNome"));
    }

    @Test
    void inativosPodemSerSelecionadosParaFiltrosMasNaoParaAtribuicao() throws Exception {
        String resposta = body(request("GET", "/os/responsaveis?incluirInativos=true", autor, null, 200));
        List<Map<String, Object>> usuarios = JsonPath.read(resposta, "$");
        assertEquals(4, usuarios.size());
        assertTrue(usuarios.stream().anyMatch(u -> inativoId.equals(((Number) u.get("id")).longValue())));
        assertFalse(resposta.contains("senha"));
        long id = os(autor, autorId, 1);
        request("PUT", "/os/" + id + "/responsavel", admin, Map.of("responsavelId", inativoId), 400);
    }

    @Test
    void edicaoDaTelaSemSituacaoPreservaAndamentoEHistorico() throws Exception {
        long id = os(autor, autorId, 1);
        request("PUT", "/os/" + id + "/situacao", autor, Map.of("situacao", "EM_ANDAMENTO"), 200);
        Map<String, Object> dados = payload(null, 1);
        dados.remove("situacao");
        dados.put("cor", "Verde");
        request("PUT", "/os/atualizar/" + id, autor, dados, 200);
        String resposta = body(request("GET", "/os/" + id, autor, null, 200));
        assertEquals("EM_ANDAMENTO", JsonPath.read(resposta, "$.situacao"));
        assertEquals("Verde", JsonPath.read(resposta, "$.cor"));
        assertEquals(1, quantidadeEvento(id, "EDICAO_CAMPOS"));
    }

    @Test
    void endpointsNovosExigemSessaoAutenticada() throws Exception {
        request("GET", "/os/responsaveis", null, null, 401);
        request("GET", "/os/relatorio", null, null, 401);
        request("GET", "/os/1/comentarios", null, null, 401);
        request("POST", "/os/1/reabrir", null, null, 401);
    }
}
