package br.com.sistemaos.domain.applicationservice;

import br.com.sistemaos.domain.entity.Peca;
import br.com.sistemaos.domain.exception.PecaDuplicadoException;
import br.com.sistemaos.domain.exception.PecaNaoEncontradoExpection;
import br.com.sistemaos.domain.model.Status;
import br.com.sistemaos.domain.repository.PecaRepository;
import br.com.sistemaos.infraestrura.dto.PecaDTO;
import br.com.sistemaos.infraestrura.dto.SalvarPecaDTO;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@AllArgsConstructor
public class PecaService {
    private final PecaRepository pecaRepository;

    @Transactional
    public Peca adicionarPeca(SalvarPecaDTO salvarPecaDTO) {
        if (existsProdutoComNome(salvarPecaDTO.getNome(), null)) {
            throw new PecaDuplicadoException(salvarPecaDTO.getNome());
        }
        Peca peca = Peca.builder()
                .nome(salvarPecaDTO.getNome())
                .quantidade(salvarPecaDTO.getQuantidade())
                .preco(salvarPecaDTO.getPreco())
                .status(Status.ATIVO)
                .build();

        pecaRepository.save(peca);
        return peca;
    }

    public Map<String, Object> listarPecas(String nome, List<Status> status, Pageable pageable) {
        Page<Peca> listaPecas;
        if (!Objects.isNull(nome) || !Objects.isNull(status)) {
            listaPecas = pecaRepository.listarPecas(nome, status, pageable);
        } else {
            listaPecas = pecaRepository.findAll(pageable);
        }
        List<PecaDTO> valor = listaPecas.getContent().stream()
                .map(PecaDTO::criar)
                .toList();

        Map<String, Object> resposta = new HashMap<>();
        resposta.put("listaEstoque", valor);
        resposta.put("total", listaPecas.getTotalElements());
        return resposta;
    }

    public List<Peca> listarPecasOs(String descricao) {
        List<Peca> pecas = pecaRepository.listaPecasByNome(descricao, Status.ATIVO);
        if (Objects.isNull(pecas)) {
            return new ArrayList<>();
        }
        return pecas;
    }

    public Peca carregarProdutoPorId (Long id) {
        return pecaRepository.findById(id).orElseThrow(() -> new PecaNaoEncontradoExpection(id));
    }

    @Transactional
    public Peca atualizarPeca(Long id, SalvarPecaDTO salvarPecaDTO) {
        if (existsProdutoComNome(salvarPecaDTO.getNome(), id)) {
            throw new PecaDuplicadoException(salvarPecaDTO.getNome());
        }
        Peca peca = carregarProdutoPorId(id);
        peca.setNome(salvarPecaDTO.getNome());
        peca.setPreco(salvarPecaDTO.getPreco());
        peca.setQuantidade(salvarPecaDTO.getQuantidade());

        return peca;
    }

    @Transactional
    public Peca atualizarStatus(Long id) {
        Peca peca = carregarProdutoPorId(id);
        peca.setStatus(trocarStatus(peca));
        return peca;
    }

    private boolean existsProdutoComNome(String nome, Long idExluido) {
        Optional<Peca> op = pecaRepository.findByNome(nome);
        if (op.isEmpty()) {
            return false;
        }
        Peca peca = op.get();
        if (peca.getId().equals(idExluido)) {
            return false;
        }
        return true;
    }

    private Status trocarStatus(Peca peca) {
        if (peca.getStatus().equals(Status.ATIVO)) {
            return Status.INATIVO;
        } else {
            return  Status.ATIVO;
        }
    }
}