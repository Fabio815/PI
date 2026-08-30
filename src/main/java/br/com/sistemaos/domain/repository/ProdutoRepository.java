package br.com.sistemaos.domain.repository;

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
    Optional<Produto> findByNome(String nome);

    @Query("""
    SELECT p
    FROM Produto p
    WHERE (:nome IS NULL OR LOWER(p.nome) LIKE LOWER(CONCAT('%', :nome, '%')))
      AND (:status IS NULL OR p.status in :status)
    """)
    Page<Produto> listarProdutos(@Param("nome") String nome, @Param("status") List<Status> status, Pageable pageable);
}
