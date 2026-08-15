package br.com.sistemaos.repository;

import br.com.sistemaos.domain.entity.Produto;
import br.com.sistemaos.domain.model.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ProdutoRepository extends JpaRepository<Produto, Long> {
    List<Produto> findByStatus(Status status);
    Optional<Produto> findByNome(String nome);
    Optional<Produto> findByIdAndStatus(Long id, Status status);
}
