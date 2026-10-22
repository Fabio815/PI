[1mdiff --git a/src/main/java/br/com/sistemaos/domain/applicationservice/OsService.java b/src/main/java/br/com/sistemaos/domain/applicationservice/OsService.java[m
[1mindex 56773a4..fd682d6 100644[m
[1m--- a/src/main/java/br/com/sistemaos/domain/applicationservice/OsService.java[m
[1m+++ b/src/main/java/br/com/sistemaos/domain/applicationservice/OsService.java[m
[36m@@ -16,7 +16,10 @@[m [mimport org.springframework.data.domain.Page;[m
 import org.springframework.data.domain.Pageable;[m
 import org.springframework.stereotype.Service;[m
 [m
[32m+[m[32mimport java.math.BigDecimal;[m
[32m+[m[32mimport java.math.RoundingMode;[m
 import java.time.LocalDate;[m
[32m+[m[32mimport java.util.ArrayList;[m
 import java.util.HashMap;[m
 import java.util.List;[m
 import java.util.Map;[m
[36m@@ -32,10 +35,11 @@[m [mpublic class OsService {[m
     private final UsuarioLogadoService usuarioLogadoService;[m
 [m
     @Transactional[m
[31m-    public Os adicionarOs(SalvarOsDTO salvarOsDTO) {[m
[32m+[m[32m    public ResultadoCadastroOs adicionarOs(SalvarOsDTO salvarOsDTO) {[m
         Cliente cliente = clienteService.carregarCliente(salvarOsDTO.getClienteId());[m
         Usuario usuario = usuarioLogadoService.obterUsuarioLogado();[m
[31m-        Orcamento orcamento = montarOrcamento(salvarOsDTO.getOrcamento());[m
[32m+[m[32m        List<String> avisosEstoque = new ArrayList<>();[m
[32m+[m[32m        Orcamento orcamento = montarOrcamento(salvarOsDTO.getOrcamento(), true, avisosEstoque);[m
 [m
         Os os = Os.builder()[m
                 .dataEmissao(LocalDate.now())[m
[36m@@ -49,7 +53,7 @@[m [mpublic class OsService {[m
                 .build();[m
         osRepository.save(os);[m
 [m
[31m-        return os;[m
[32m+[m[32m        return new ResultadoCadastroOs(os, List.copyOf(avisosEstoque));[m
     }[m
 [m
     public Map<String, Object> listarOs(Long id, String nome, List<Status> status, Pageable pageable) {[m
[36m@@ -113,17 +117,26 @@[m [mpublic class OsService {[m
     }[m
 [m
     private Orcamento montarOrcamento(SalvarOrcamentoDTO dto) {[m
[32m+[m[32m        return montarOrcamento(dto, false, new ArrayList<>());[m
[32m+[m[32m    }[m
[32m+[m
[32m+[m[32m    private Orcamento montarOrcamento([m
[32m+[m[32m            SalvarOrcamentoDTO dto,[m
[32m+[m[32m            boolean baixarEstoque,[m
[32m+[m[32m            List<String> avisosEstoque) {[m
         List<ItemOrcamento> itens = dto.getItens().stream()[m
[31m-                .map(this::montarItem)[m
[32m+[m[32m                .map(itemDto -> montarItem(itemDto, baixarEstoque, avisosEstoque))[m
                 .toList();[m
 [m
[31m-        double valorPecas = itens.stream().mapToDouble(ItemOrcamento::getValorTotal).sum();[m
[31m-        double valorServico = Optional.ofNullable(dto.getValorServico()).orElse(0.0);[m
[32m+[m[32m        double valorPecas = arredondarMoeda([m
[32m+[m[32m                itens.stream().mapToDouble(ItemOrcamento::getValorTotal).sum());[m
[32m+[m[32m        double valorServico = arredondarMoeda([m
[32m+[m[32m                Optional.ofNullable(dto.getValorServico()).orElse(0.0));[m
 [m
         Orcamento orcamento = new Orcamento();[m
         orcamento.setValorServico(valorServico);[m
         orcamento.setObservacoes(dto.getObservacoes());[m
[31m-        orcamento.setValorTotal(valorPecas + valorServico);[m
[32m+[m[32m        orcamento.setValorTotal(arredondarMoeda(valorPecas + valorServico));[m
         orcamento.setItemOrcamento(itens);[m
 [m
         itens.forEach(item -> item.setOrcamento(orcamento));[m
[36m@@ -131,18 +144,51 @@[m [mpublic class OsService {[m
         return orcamento;[m
     }[m
 [m
[31m-    private ItemOrcamento montarItem(SalvarItemOrcamentoDTO dto) {[m
[31m-        Peca peca = pecaService.carregarPeca(dto.getPecaId());[m
[32m+[m[32m    private ItemOrcamento montarItem([m
[32m+[m[32m            SalvarItemOrcamentoDTO dto,[m
[32m+[m[32m            boolean baixarEstoque,[m
[32m+[m[32m            List<String> avisosEstoque) {[m
[32m+[m[32m        Peca peca = baixarEstoque[m
[32m+[m[32m                ? pecaService.carregarPecaParaBaixa(dto.getPecaId())[m
[32m+[m[32m                : pecaService.carregarPeca(dto.getPecaId());[m
[32m+[m
[32m+[m[32m        int quantidadeSolicitada = dto.getQuantidade();[m
[32m+[m
[32m+[m[32m        if (baixarEstoque) {[m
[32m+[m[32m            int quantidadeDisponivel = peca.getQuantidade();[m
[32m+[m
[32m+[m[32m            if (quantidadeDisponivel < quantidadeSolicitada) {[m
[32m+[m[32m                int quantidadeFaltante = quantidadeSolicitada - quantidadeDisponivel;[m
[32m+[m[32m                avisosEstoque.add(String.format([m
[32m+[m[32m                        "Peça '%s': solicitada %d, disponível %d, faltam %d unidade(s).",[m
[32m+[m[32m                        peca.getNome(),[m
[32m+[m[32m                        quantidadeSolicitada,[m
[32m+[m[32m                        quantidadeDisponivel,[m
[32m+[m[32m                        quantidadeFaltante));[m
[32m+[m[32m            }[m
[32m+[m
[32m+[m[32m            // A OS não é bloqueada por insuficiência. A baixa integral registra o déficit[m
[32m+[m[32m            // como estoque negativo, preservando exatamente a quantidade usada na OS.[m
[32m+[m[32m            peca.setQuantidade(quantidadeDisponivel - quantidadeSolicitada);[m
[32m+[m[32m        }[m
[32m+[m
[32m+[m[32m        double valorUnitario = arredondarMoeda(peca.getPreco());[m
 [m
         ItemOrcamento item = new ItemOrcamento();[m
[31m-        item.setQuantidade(dto.getQuantidade());[m
[31m-        item.setValorUnitario(peca.getPreco());[m
[31m-        item.setValorTotal(peca.getPreco() * dto.getQuantidade());[m
[32m+[m[32m        item.setQuantidade(quantidadeSolicitada);[m
[32m+[m[32m        item.setValorUnitario(valorUnitario);[m
[32m+[m[32m        item.setValorTotal(arredondarMoeda(valorUnitario * quantidadeSolicitada));[m
         item.setItem(peca);[m
 [m
         return item;[m
     }[m
 [m
[32m+[m[32m    private double arredondarMoeda(Double valor) {[m
[32m+[m[32m        return BigDecimal.valueOf(valor)[m
[32m+[m[32m                .setScale(2, RoundingMode.HALF_UP)[m
[32m+[m[32m                .doubleValue();[m
[32m+[m[32m    }[m
[32m+[m
     private Status trocarStatus(Os os) {[m
         if (os.getStatus().equals(Status.ATIVO)) {[m
             return Status.INATIVO;[m
[36m@@ -150,4 +196,7 @@[m [mpublic class OsService {[m
             return  Status.ATIVO;[m
         }[m
     }[m
[32m+[m
[32m+[m[32m    public record ResultadoCadastroOs(Os os, List<String> avisosEstoque) {[m
[32m+[m[32m    }[m
 }[m
\ No newline at end of file[m
[1mdiff --git a/src/main/java/br/com/sistemaos/domain/applicationservice/PecaService.java b/src/main/java/br/com/sistemaos/domain/applicationservice/PecaService.java[m
[1mindex 5eb1b76..28ef8dc 100644[m
[1m--- a/src/main/java/br/com/sistemaos/domain/applicationservice/PecaService.java[m
[1m+++ b/src/main/java/br/com/sistemaos/domain/applicationservice/PecaService.java[m
[36m@@ -115,4 +115,9 @@[m [mpublic class PecaService {[m
         return pecaRepository.findById(id)[m
                 .orElseThrow(() -> new EntityNotFoundException("Peça não encontrada: " + id));[m
     }[m
[32m+[m
[32m+[m[32m    public Peca carregarPecaParaBaixa(Long id) {[m
[32m+[m[32m        return pecaRepository.buscarPorIdParaBaixa(id)[m
[32m+[m[32m                .orElseThrow(() -> new EntityNotFoundException("Peça não encontrada: " + id));[m
[32m+[m[32m    }[m
 }[m
\ No newline at end of file[m
[1mdiff --git a/src/main/java/br/com/sistemaos/domain/repository/PecaRepository.java b/src/main/java/br/com/sistemaos/domain/repository/PecaRepository.java[m
[1mindex e118af0..8813ac2 100644[m
[1m--- a/src/main/java/br/com/sistemaos/domain/repository/PecaRepository.java[m
[1m+++ b/src/main/java/br/com/sistemaos/domain/reposito