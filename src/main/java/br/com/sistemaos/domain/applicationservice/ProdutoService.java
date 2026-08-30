package br.com.sistemaos.domain.applicationservice;

import br.com.sistemaos.domain.entity.Produto;
import br.com.sistemaos.domain.model.Resposta;
import br.com.sistemaos.infraestrura.dto.ProdutoDTO;
import br.com.sistemaos.domain.exception.ProdutoDuplicadoException;
import br.com.sistemaos.domain.exception.ProdutoNaoEncontradoExpection;
import br.com.sistemaos.domain.repository.ProdutoRepository;
import br.com.sistemaos.domain.model.Status;
import br.com.sistemaos.infraestrura.dto.SalvarProdutoDTO;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ProdutoService {
    private final ProdutoRepository produtoRepository;

    @Transactional
    public Produto criarProduto(SalvarProdutoDTO salvarProdutoDTO) {
        if (existsProdutoComNome(salvarProdutoDTO.getNome(), null)) {
            throw new ProdutoDuplicadoException(salvarProdutoDTO.getNome());
        }
        Produto produto = Produto.builder()
                .nome(salvarProdutoDTO.getNome())
                .quantidade(salvarProdutoDTO.getQuantidade())
                .preco(salvarProdutoDTO.getPreco())
                .status(Status.ATIVO)
                .build();

        produtoRepository.save(produto);
        return produto;
    }

    public Page<Produto> listarProdutos(String nome, List<Status> status, Pageable pageable) {
        return produtoRepository.listarProdutos(nome, status, pageable);
    }

    public Produto carregarProdutoPorId (Long id) {
        return produtoRepository.findById(id).orElseThrow(() -> new ProdutoNaoEncontradoExpection(id));
    }

    @Transactional
    public Produto atualizarProduto(Long id, SalvarProdutoDTO salvarProdutoDTO) {
        if (existsProdutoComNome(salvarProdutoDTO.getNome(), id)) {
            throw new ProdutoDuplicadoException(salvarProdutoDTO.getNome());
        }
        Produto produto = carregarProdutoPorId(id);
        produto.setNome(salvarProdutoDTO.getNome());
        produto.setPreco(salvarProdutoDTO.getPreco());
        produto.setQuantidade(salvarProdutoDTO.getQuantidade());

        return produto;
    }

    @Transactional
    public Resposta atualizarStatus(ProdutoDTO produto) {
        return null;
    }

    private boolean existsProdutoComNome(String nome, Long idExluido) {
        Optional<Produto> op = produtoRepository.findByNome(nome);
        if (op.isEmpty()) {
            return false;
        }
        Produto produto = op.get();
        if (produto.getId().equals(idExluido)) {
            return false;
        }
        return true;
    }
}