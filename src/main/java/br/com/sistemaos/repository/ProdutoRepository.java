package br.com.sistemaos.repository;

import br.com.sistemaos.domain.entity.Produto;
import br.com.sistemaos.domain.model.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProdutoRepository extends JpaRepository<Produto, Long> {
    List<Produto> findByStatus(Status status);
    Optional<Produto> findByNome(String nome);
    Optional<Produto> findByIdAndStatus(Long id, Status status);

    @Query("""
    SELECT p
    FROM Produto p
    WHERE (:nome IS NULL OR LOWER(p.nome) LIKE LOWER(CONCAT('%', :nome, '%')))
      AND (:status IS NULL OR p.status = :status)
    """)
    Page<Produto> listarProdutos(@Param("nome") String nome, @Param("status") Status status, Pageable pageable);
}
