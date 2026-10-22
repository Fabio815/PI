package br.com.sistemaos;

import br.com.sistemaos.domain.entity.Cliente;
import br.com.sistemaos.domain.entity.Peca;
import br.com.sistemaos.domain.entity.Usuario;
import br.com.sistemaos.domain.model.Perfil;
import br.com.sistemaos.domain.model.Status;
import com.jayway.jsonpath.JsonPath;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.transaction.support.TransactionTemplate;
import tools.jackson.databind.ObjectMapper;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
abstract class SprintOsTestSupport {
    @Autowired protected MockMvc mvc;
    @Autowired protected ObjectMapper json;
    @Autowired protected JdbcTemplate jdbc;
    @Autowired protected EntityManager em;
    @Autowired protected TransactionTemplate tx;
    @Autowired protected PasswordEncoder encoder;
    protected Long adminId, autorId, outroId, inativoId, clienteId, outroClienteId, pecaId;
    protected MockHttpSession admin, autor, outro;

    @BeforeEach
    void prepararBancoIsolado() throws Exception {
        try (var connection = jdbc.getDataSource().getConnection()) {
            assertTrue(connection.getMetaData().getURL().startsWith("jdbc:h2:mem:"),
                    "A suíte deve executar exclusivamente em H2 em memória.");
        }
        // A suíte NÃO executa em paralelo. Cada caso confirma transações reais da API.
        jdbc.execute("SET REFERENTIAL_INTEGRITY FALSE");
        try {
            List<String> tables = jdbc.queryForList(
                    "select table_name from information_schema.tables " +
                    "where lower(table_schema) = 'public' and table_type = 'BASE TABLE'", String.class);
            for (String table : tables) {
                jdbc.execute("TRUNCATE TABLE \"" + table.replace("\"", "\"\"") + "\" RESTART IDENTITY");
            }
        } finally {
            jdbc.execute("SET REFERENTIAL_INTEGRITY TRUE");
        }
        tx.executeWithoutResult(s -> {
            adminId = usuario("Admin", "admin@test.local", Perfil.ADM, Status.ATIVO).getId();
            autorId = usuario("Autor", "autor@test.local", Perfil.FUNCIONARIO, Status.ATIVO).getId();
            outroId = usuario("Outro", "outro@test.local", Perfil.FUNCIONARIO, Status.ATIVO).getId();
            inativoId = usuario("Inativo", "inativo@test.local", Perfil.FUNCIONARIO, Status.INATIVO).getId();
            Cliente cliente = Cliente.builder().nome("Cliente Alfa").telefone("11911111111")
                    .status(Status.ATIVO).build();
            Cliente outroCliente = Cliente.builder().nome("Cliente Beta").telefone("11922222222")
                    .status(Status.ATIVO).build();
            em.persist(cliente);
            em.persist(outroCliente);
            clienteId = cliente.getId();
            outroClienteId = outroCliente.getId();
            Peca peca = Peca.builder().nome("Câmara de teste").quantidade(20).preco(25.0)
                    .status(Status.ATIVO).build();
            em.persist(peca);
            pecaId = peca.getId();
        });
        admin = login("admin@test.local");
        autor = login("autor@test.local");
        outro = login("outro@test.local");
    }

    private Usuario usuario(String nome, String email, Perfil perfil, Status status) {
        Usuario usuario = Usuario.builder().nome(nome).email(email).senha(encoder.encode("Teste-123"))
                .chave(perfil).status(status).build();
        em.persist(usuario);
        return usuario;
    }

    protected MockHttpSession login(String email) throws Exception {
        MvcResult result = mvc.perform(post("/usuarios/login").contentType(MediaType.APPLICATION_JSON)
                        .content(json.writeValueAsString(Map.of("email", email, "senha", "Teste-123"))))
                .andExpect(status().isOk()).andReturn();
        return (MockHttpSession) result.getRequest().getSession(false);
    }

    protected Map<String, Object> payload(Long responsavel, int quantidade) {
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("clienteId", clienteId);
        dto.put("modelo", "Bicicleta teste");
        dto.put("cor", "Azul");
        dto.put("situacao", "PENDENTE");
        dto.put("orcamento", Map.of("valorServico", 100.50, "observacoes", "Revisão",
                "itens", List.of(Map.of("pecaId", pecaId, "quantidade", quantidade, "valorUnitario", 25.0))));
        if (responsavel != null) dto.put("responsavelId", responsavel);
        return dto;
    }

    protected MvcResult request(String method, String url, MockHttpSession session, Object body,
                                int expected) throws Exception {
        MockHttpServletRequestBuilder builder = switch (method) {
            case "POST" -> post(url);
            case "PUT" -> put(url);
            case "DELETE" -> delete(url);
            default -> get(url);
        };
        if (session != null) builder.session(session);
        if (body != null) builder.contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsString(body));
        return mvc.perform(builder).andExpect(status().is(expected)).andReturn();
    }

    protected String body(MvcResult result) throws Exception {
        return result.getResponse().getContentAsString(java.nio.charset.StandardCharsets.UTF_8);
    }

    protected long os(MockHttpSession session, Long responsavel, int quantidade) throws Exception {
        return numero(body(request("POST", "/os/cadastrar", session, payload(responsavel, quantidade), 201)), "$.os.id");
    }

    protected long numero(String body, String path) {
        return ((Number) JsonPath.read(body, path)).longValue();
    }

    protected int saldo() {
        return jdbc.queryForObject("select quantidade from peca where id = ?", Integer.class, pecaId);
    }

    protected List<Map<String, Object>> historico(long osId) throws Exception {
        return JsonPath.read(body(request("GET", "/os/" + osId + "/historico", autor, null, 200)), "$");
    }

    protected long quantidadeEvento(long osId, String tipo) throws Exception {
        return historico(osId).stream().filter(h -> tipo.equals(h.get("tipoEvento"))).count();
    }
}
