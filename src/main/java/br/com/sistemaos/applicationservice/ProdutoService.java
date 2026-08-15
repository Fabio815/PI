package br.com.sistemaos.applicationservice;

import br.com.sistemaos.domain.entity.Produto;
import br.com.sistemaos.exception.ProdutoDuplicadoException;
import br.com.sistemaos.repository.ProdutoRepository;
import br.com.sistemaos.domain.model.Status;
import br.com.sistemaos.dto.SalvarProdutoDTO;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
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

    public List<Produto> listarProdutos() {
        List<Produto> listaProdutos = null;
        listaProdutos = produtoRepository.findByStatus(Status.INATIVO);
        return listaProdutos;
    }

    public boolean existsProdutoComNome(String nome, Long idExluido) {
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