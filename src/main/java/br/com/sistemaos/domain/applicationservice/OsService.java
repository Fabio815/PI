package br.com.sistemaos.domain.applicationservice;

import br.com.sistemaos.domain.entity.*;
import br.com.sistemaos.domain.exception.AcessoNegadoException;
import br.com.sistemaos.domain.exception.ConflitoOperacionalException;
import br.com.sistemaos.domain.exception.OsNaoEncontradaException;
import br.com.sistemaos.domain.exception.RecursoNaoEncontradoException;
import br.com.sistemaos.domain.model.Perfil;
import br.com.sistemaos.domain.model.Status;
import br.com.sistemaos.domain.model.StatusOs;
import br.com.sistemaos.domain.model.TipoEventoHistorico;
import br.com.sistemaos.domain.repository.ComentarioOsRepository;
import br.com.sistemaos.domain.repository.OsRepository;
import br.com.sistemaos.domain.repository.UsuarioRepository;
import br.com.sistemaos.infraestrura.dto.*;
import br.com.sistemaos.infraestrura.exception.RequestException;
import br.com.sistemaos.infraestrura.security.UsuarioLogadoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;

@Service
public class OsService {
    private final OsRepository osRepository;
    private final ClienteService clienteService;
    private final PecaService pecaService;
    private final UsuarioLogadoService usuarioLogadoService;
    private final HistoricoService historicoService;
    private final UsuarioRepository usuarioRepository;
    private final ComentarioOsRepository comentarioOsRepository;

    @Autowired
    public OsService(OsRepository osRepository, ClienteService clienteService, PecaService pecaService,
                     UsuarioLogadoService usuarioLogadoService, HistoricoService historicoService,
                     UsuarioRepository usuarioRepository, ComentarioOsRepository comentarioOsRepository) {
        this.osRepository = osRepository;
        this.clienteService = clienteService;
        this.pecaService = pecaService;
        this.usuarioLogadoService = usuarioLogadoService;
        this.historicoService = historicoService;
        this.usuarioRepository = usuarioRepository;
        this.comentarioOsRepository = comentarioOsRepository;
    }

    /** Construtor legado útil a chamadores que ainda não usam atribuição/comentários. */
    public OsService(OsRepository osRepository, ClienteService clienteService, PecaService pecaService,
                     UsuarioLogadoService usuarioLogadoService, HistoricoService historicoService) {
        this(osRepository, clienteService, pecaService, usuarioLogadoService, historicoService, null, null);
    }

    @Transactional
    public ResultadoCadastroOs adicionarOs(SalvarOsDTO dto) {
        Usuario criador = usuarioLogadoService.obterUsuarioLogado();
        if (criador == null) throw new AcessoNegadoException("Autenticação necessária para criar uma OS");
        Cliente cliente = clienteService.carregarCliente(dto.getClienteId());
        Usuario responsavel = dto.getResponsavelId() == null
                ? criador : carregarResponsavelAtivo(dto.getResponsavelId());
        List<String> avisos = new ArrayList<>();
        Orcamento orcamento = montarOrcamento(dto.getOrcamento(), true, avisos);

        Os os = Os.builder().dataEmissao(LocalDate.now()).situacao(StatusOs.PENDENTE)
                .cliente(cliente).usuario(criador).responsavel(responsavel).orcamento(orcamento)
                .status(Status.ATIVO).modelo(dto.getModelo()).cor(dto.getCor()).build();
        Os salva = osRepository.save(os);
        historicoService.salvarTodos(List.of(
                HistoricoService.novoEvento(salva, criador, TipoEventoHistorico.CRIACAO,
                        "OS", null, "Criada", "Ordem de serviço criada", null, null),
                HistoricoService.novoEvento(salva, criador, TipoEventoHistorico.ATRIBUICAO,
                        "Responsável", null, rotulo(responsavel), "Responsável inicial atribuído", null, responsavel)), salva);
        return new ResultadoCadastroOs(salva, List.copyOf(avisos));
    }

    public Map<String, Object> listarOs(Long id, String nome, List<Status> status, Pageable pageable) {
        Page<OsListagemDTO> pagina = osRepository.listarOs(id, nome, status, pageable);
        Map<String, Object> resposta = new HashMap<>();
        resposta.put("listaOs", pagina.getContent());
        resposta.put("total", pagina.getTotalElements());
        return resposta;
    }

    @Transactional
    public Os atualizarStatus(Long id) {
        Os os = carregarOsParaAtualizacao(id);
        Usuario ator = usuarioLogadoService.obterUsuarioLogado();
        validarPermissaoEdicao(os, ator);
        validarNaoEncerrada(os);
        Status antes = os.getStatus();
        Status depois = antes == Status.ATIVO ? Status.INATIVO : Status.ATIVO;
        if (antes == depois) return os;
        os.setStatus(depois);
        Os salva = osRepository.save(os);
        historicoService.salvarTodos(historicoService.gerarHistoricoStatus(antes, depois, ator), salva);
        return salva;
    }

    @Transactional
    public Os atualizarOs(Long id, SalvarOsDTO dto) {
        Os os = carregarOsParaAtualizacao(id);
        Usuario ator = usuarioLogadoService.obterUsuarioLogado();
        validarPermissaoEdicao(os, ator);
        validarNaoEncerrada(os);
        Usuario responsavelAtual = responsavelEfetivo(os);
        if (dto.getResponsavelId() != null && !Objects.equals(dto.getResponsavelId(), id(responsavelAtual))) {
            if (!isAdmin(ator)) throw new AcessoNegadoException("Somente ADM pode reatribuir a ordem de serviço");
            throw new RequestException("Use o endpoint /os/{id}/responsavel para atribuir responsável", "Use endpoint dedicado");
        }
        if (dto.getSituacao() != null && dto.getSituacao() != os.getSituacao()) {
            throw new RequestException("Altere a situação pelo endpoint /os/{id}/situacao", "Use endpoint dedicado");
        }

        Cliente cliente = clienteService.carregarCliente(dto.getClienteId());
        Orcamento proposta = montarOrcamento(dto.getOrcamento());
        List<HistoricoOs> eventos = historicoService.compararEGerarHistorico(os, dto, cliente, ator, proposta);
        os.setModelo(dto.getModelo());
        os.setCor(dto.getCor());
        os.setCliente(cliente);
        aplicarOrcamento(os, proposta);
        Os salva = osRepository.save(os);
        historicoService.salvarTodos(eventos, salva);
        return salva;
    }

    @Transactional
    public Os atribuirResponsavel(Long id, Long responsavelId) {
        Usuario ator = usuarioLogadoService.obterUsuarioLogado();
        if (!isAdmin(ator)) throw new AcessoNegadoException("Somente ADM pode atribuir ou reatribuir responsável");
        Os os = carregarOsParaAtualizacao(id);
        Usuario anterior = responsavelEfetivo(os);
        Usuario novo = carregarResponsavelAtivo(responsavelId);
        if (Objects.equals(id(anterior), id(novo))) return os;
        os.setResponsavel(novo);
        Os salva = osRepository.save(os);
        TipoEventoHistorico tipo = anterior == null ? TipoEventoHistorico.ATRIBUICAO : TipoEventoHistorico.REATRIBUICAO;
        HistoricoOs evento = HistoricoService.novoEvento(salva, ator, tipo, "Responsável",
                rotulo(anterior), rotulo(novo), tipo == TipoEventoHistorico.ATRIBUICAO
                        ? "Responsável atribuído" : "Responsável reatribuído", anterior, novo);
        historicoService.salvarTodos(List.of(evento), salva);
        return salva;
    }

    @Transactional
    public Os atualizarSituacao(Long id, StatusOs novaSituacao) {
        if (novaSituacao == null) throw new RequestException("Situação é obrigatória", "Erro de validação");
        Os os = carregarOsParaAtualizacao(id);
        Usuario ator = usuarioLogadoService.obterUsuarioLogado();
        validarPermissaoEdicao(os, ator);
        validarNaoEncerrada(os);
        StatusOs anterior = os.getSituacao();
        if (anterior == novaSituacao) return os;
        os.setSituacao(novaSituacao);
        boolean encerrando = novaSituacao == StatusOs.CONCLUIDO || novaSituacao == StatusOs.CANCELADO;
        os.setDataFim(encerrando ? LocalDate.now() : null);
        Os salva = osRepository.save(os);
        TipoEventoHistorico tipo = novaSituacao == StatusOs.CONCLUIDO ? TipoEventoHistorico.CONCLUSAO
                : novaSituacao == StatusOs.CANCELADO ? TipoEventoHistorico.CANCELAMENTO
                : TipoEventoHistorico.ALTERACAO_STATUS;
        String descricao = tipo == TipoEventoHistorico.CONCLUSAO ? "Ordem de serviço concluída"
                : tipo == TipoEventoHistorico.CANCELAMENTO ? "Ordem de serviço cancelada" : "Situação alterada";
        historicoService.salvarTodos(List.of(HistoricoService.novoEvento(salva, ator, tipo,
                "Situação", nome(anterior), nome(novaSituacao), descricao, null, null)), salva);
        return salva;
    }

    @Transactional
    public Os reabrir(Long id) {
        Usuario ator = usuarioLogadoService.obterUsuarioLogado();
        if (!isAdmin(ator)) throw new AcessoNegadoException("Somente ADM pode reabrir uma ordem de serviço");
        Os os = carregarOsParaAtualizacao(id);
        if (!encerrada(os)) throw new ConflitoOperacionalException("A ordem de serviço não está encerrada");
        StatusOs anterior = os.getSituacao();
        os.setSituacao(StatusOs.PENDENTE);
        os.setDataFim(null);
        Os salva = osRepository.save(os);
        historicoService.salvarTodos(List.of(HistoricoService.novoEvento(salva, ator,
                TipoEventoHistorico.REABERTURA, "Situação", nome(anterior), StatusOs.PENDENTE.name(),
                "Ordem de serviço reaberta; estoque não foi baixado novamente", null, null)), salva);
        return salva;
    }

    @Transactional(readOnly = true)
    public List<UsuarioRefDTO> listarResponsaveis() {
        return listarResponsaveis(false);
    }

    @Transactional(readOnly = true)
    public List<UsuarioRefDTO> listarResponsaveis(boolean incluirInativos) {
        List<Usuario> usuarios = incluirInativos
                ? usuarioRepository.findAll(org.springframework.data.domain.Sort.by("nome", "id"))
                : usuarioRepository.findByStatusOrderByNomeAscIdAsc(Status.ATIVO);
        return usuarios.stream()
                .map(UsuarioRefDTO::criar).toList();
    }

    @Transactional(readOnly = true)
    public List<HistoricoOsDTO> listarHistorico(Long id, String tipoEvento, LocalDate dataInicio,
                                                LocalDate dataFim, Long usuarioId) {
        carregarOs(id);
        if (dataInicio != null && dataFim != null && dataInicio.isAfter(dataFim))
            throw new RequestException("dataInicio não pode ser posterior a dataFim", "Intervalo inválido");
        TipoEventoHistorico tipo = null;
        if (tipoEvento != null && !tipoEvento.isBlank()) {
            try { tipo = TipoEventoHistorico.valueOf(tipoEvento.trim().toUpperCase(Locale.ROOT)); }
            catch (IllegalArgumentException ex) { throw new RequestException("tipoEvento inválido", "Filtro inválido"); }
        }
        final TipoEventoHistorico filtroTipo = tipo;
        return historicoService.registrosPorOs(id).stream()
                .filter(h -> filtroTipo == null || historicoService.tipoEfetivo(h) == filtroTipo)
                .filter(h -> dataInicio == null || !h.getDataAlteracao().toLocalDate().isBefore(dataInicio))
                .filter(h -> dataFim == null || !h.getDataAlteracao().toLocalDate().isAfter(dataFim))
                .filter(h -> usuarioId == null || (h.getUsuario() != null && Objects.equals(h.getUsuario().getId(), usuarioId)))
                .map(historicoService::dto).toList();
    }

    @Transactional(readOnly = true)
    public List<ComentarioOsDTO> listarComentarios(Long osId) {
        carregarOs(osId);
        return comentarioOsRepository.findByOsIdOrderByDataCriacaoAscIdAsc(osId).stream()
                .map(ComentarioOsDTO::criar).toList();
    }

    @Transactional
    public ComentarioOsDTO criarComentario(Long osId, SalvarComentarioDTO dto) {
        Usuario ator = usuarioLogadoService.obterUsuarioLogado();
        String conteudo = validarConteudoComentario(dto == null ? null : dto.getConteudo());
        Os os = carregarOsParaAtualizacao(osId);
        ComentarioOs comentario = comentarioOsRepository.save(ComentarioOs.builder().os(os).autor(ator)
                .conteudo(conteudo).dataCriacao(java.time.LocalDateTime.now()).build());
        historicoService.salvarTodos(List.of(HistoricoService.novoEvento(os, ator,
                TipoEventoHistorico.CRIACAO_COMENTARIO, "Comentário #" + comentario.getId(), null, conteudo,
                "Comentário #" + comentario.getId() + " criado", null, null)), os);
        return ComentarioOsDTO.criar(comentario);
    }

    @Transactional
    public ComentarioOsDTO editarComentario(Long osId, Long comentarioId, SalvarComentarioDTO dto) {
        Usuario ator = usuarioLogadoService.obterUsuarioLogado();
        String conteudo = validarConteudoComentario(dto == null ? null : dto.getConteudo());
        Os os = carregarOsParaAtualizacao(osId);
        ComentarioOs comentario = comentarioOsRepository.findByIdAndOsIdForUpdate(comentarioId, osId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Comentário não encontrado"));
        if (!isAdmin(ator) && (comentario.getAutor() == null || !Objects.equals(comentario.getAutor().getId(), id(ator))))
            throw new AcessoNegadoException("Somente o autor ou ADM pode editar este comentário");
        if (Objects.equals(comentario.getConteudo(), conteudo)) return ComentarioOsDTO.criar(comentario);
        String anterior = comentario.getConteudo();
        comentario.setConteudo(conteudo);
        comentario.setEditadoPor(ator);
        comentario.setDataEdicao(java.time.LocalDateTime.now());
        comentarioOsRepository.save(comentario);
        historicoService.salvarTodos(List.of(HistoricoService.novoEvento(os, ator,
                TipoEventoHistorico.EDICAO_COMENTARIO, "Comentário #" + comentarioId,
                anterior, conteudo, "Comentário #" + comentarioId + " editado", null, null)), os);
        return ComentarioOsDTO.criar(comentario);
    }

    @Transactional(readOnly = true)
    public Os carregarOs(Long id) {
        return osRepository.findById(id).orElseThrow(() -> new OsNaoEncontradaException(id));
    }

    private Os carregarOsParaAtualizacao(Long id) {
        return osRepository.findByIdForUpdate(id).orElseThrow(() -> new OsNaoEncontradaException(id));
    }

    private Usuario carregarResponsavelAtivo(Long responsavelId) {
        if (responsavelId == null || responsavelId <= 0)
            throw new RequestException("Responsável inválido", "Erro de validação");
        if (usuarioRepository == null) throw new IllegalStateException("Repositório de usuários indisponível");
        Usuario u = usuarioRepository.findByIdForResponsavelUpdate(responsavelId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário responsável não encontrado"));
        if (u.getStatus() != Status.ATIVO)
            throw new RequestException("Somente usuários ativos podem ser responsáveis", "Responsável inativo");
        return u;
    }

    private void validarPermissaoEdicao(Os os, Usuario ator) {
        if (isAdmin(ator)) return;
        if (ator == null || os.getUsuario() == null || !Objects.equals(os.getUsuario().getId(), ator.getId()))
            throw new AcessoNegadoException("Você só pode alterar as ordens de serviço que você criou");
    }

    private void validarNaoEncerrada(Os os) {
        if (encerrada(os)) throw new ConflitoOperacionalException("A ordem de serviço está encerrada; somente ADM pode reabri-la");
    }

    private boolean encerrada(Os os) {
        return os.getDataFim() != null || os.getSituacao() == StatusOs.CONCLUIDO || os.getSituacao() == StatusOs.CANCELADO;
    }

    private boolean isAdmin(Usuario usuario) { return usuario != null && usuario.getChave() == Perfil.ADM; }
    private Usuario responsavelEfetivo(Os os) { return os.getResponsavel() != null ? os.getResponsavel() : os.getUsuario(); }
    private Long id(Usuario u) { return u == null ? null : u.getId(); }
    private String rotulo(Usuario u) { return u == null ? null : u.getId() + " - " + u.getNome(); }
    private String nome(Enum<?> valor) { return valor == null ? null : valor.name(); }

    private String validarConteudoComentario(String texto) {
        if (texto == null) throw new RequestException("Comentário não pode ficar vazio", "Erro de validação");
        String normalizado = texto.trim();
        if (normalizado.isEmpty()) throw new RequestException("Comentário não pode ficar vazio", "Erro de validação");
        if (normalizado.length() > 4000) throw new RequestException("Comentário deve ter no máximo 4000 caracteres", "Erro de validação");
        return normalizado;
    }

    private Orcamento montarOrcamento(SalvarOrcamentoDTO dto) {
        return montarOrcamento(dto, false, new ArrayList<>());
    }

    private Orcamento montarOrcamento(SalvarOrcamentoDTO dto, boolean baixarEstoque, List<String> avisos) {
        if (dto == null || dto.getItens() == null || dto.getItens().isEmpty())
            throw new RequestException("Orçamento precisa ter ao menos um item", "Erro de validação");
        List<ItemOrcamento> itens = dto.getItens().stream().map(i -> montarItem(i, baixarEstoque, avisos)).toList();
        double valorPecas = arredondarMoeda(itens.stream().mapToDouble(ItemOrcamento::getValorTotal).sum());
        double valorServico = arredondarMoeda(Optional.ofNullable(dto.getValorServico()).orElse(0.0));
        Orcamento o = new Orcamento();
        o.setValorServico(valorServico);
        o.setObservacoes(dto.getObservacoes());
        o.setValorTotal(arredondarMoeda(valorPecas + valorServico));
        o.setItemOrcamento(new ArrayList<>(itens));
        itens.forEach(i -> i.setOrcamento(o));
        return o;
    }

    private void aplicarOrcamento(Os os, Orcamento proposta) {
        Orcamento atual = os.getOrcamento();
        if (atual == null) { os.setOrcamento(proposta); return; }
        atual.setValorServico(proposta.getValorServico());
        atual.setObservacoes(proposta.getObservacoes());
        atual.setValorTotal(proposta.getValorTotal());
        List<ItemOrcamento> existentes = atual.getItemOrcamento();
        if (existentes == null) { existentes = new ArrayList<>(); atual.setItemOrcamento(existentes); }
        List<ItemOrcamento> originais = new ArrayList<>(existentes);
        List<ItemOrcamento> disponiveis = new ArrayList<>(existentes);
        Set<ItemOrcamento> mantidos = Collections.newSetFromMap(new IdentityHashMap<>());
        for (ItemOrcamento solicitado : proposta.getItemOrcamento()) {
            Long pecaId = solicitado.getItem() == null ? null : solicitado.getItem().getId();
            ItemOrcamento encontrado = disponiveis.stream().filter(i -> Objects.equals(
                    i.getItem() == null ? null : i.getItem().getId(), pecaId)).findFirst().orElse(null);
            if (encontrado == null) {
                solicitado.setOrcamento(atual);
                existentes.add(solicitado);
            } else {
                for (int i = 0; i < disponiveis.size(); i++) {
                    if (disponiveis.get(i) == encontrado) { disponiveis.remove(i); break; }
                }
                mantidos.add(encontrado);
                encontrado.setQuantidade(solicitado.getQuantidade());
                encontrado.setValorUnitario(solicitado.getValorUnitario());
                encontrado.setValorTotal(solicitado.getValorTotal());
                encontrado.setItem(solicitado.getItem());
            }
        }
        // Remover apenas as entidades antigas não correspondidas; comparação por identidade evita
        // equals/hashCode recursivos do Lombok entre ItemOrcamento e Orcamento.
        for (var iterator = existentes.iterator(); iterator.hasNext();) {
            ItemOrcamento item = iterator.next();
            boolean original = false;
            for (ItemOrcamento candidato : originais) if (candidato == item) { original = true; break; }
            if (original && !mantidos.contains(item)) iterator.remove();
        }
    }

    private ItemOrcamento montarItem(SalvarItemOrcamentoDTO dto, boolean baixarEstoque, List<String> avisos) {
        if (dto == null || dto.getPecaId() == null || dto.getQuantidade() == null || dto.getQuantidade() < 1)
            throw new RequestException("Item ou quantidade inválidos", "Erro de validação");
        Peca peca = baixarEstoque ? pecaService.carregarPecaParaBaixa(dto.getPecaId()) : pecaService.carregarPeca(dto.getPecaId());
        int quantidade = dto.getQuantidade();
        if (baixarEstoque) {
            int disponivel = peca.getQuantidade();
            if (disponivel < quantidade) avisos.add(String.format(Locale.ROOT,
                    "Peça '%s': solicitada %d, disponível %d, faltam %d unidade(s).", peca.getNome(), quantidade, disponivel, quantidade - disponivel));
            peca.setQuantidade(disponivel - quantidade);
        }
        double unitario = arredondarMoeda(peca.getPreco());
        ItemOrcamento item = new ItemOrcamento();
        item.setQuantidade(quantidade);
        item.setValorUnitario(unitario);
        item.setValorTotal(arredondarMoeda(unitario * quantidade));
        item.setItem(peca);
        return item;
    }

    private double arredondarMoeda(Double valor) {
        return BigDecimal.valueOf(valor).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }

    public record ResultadoCadastroOs(Os os, List<String> avisosEstoque) { }
}
