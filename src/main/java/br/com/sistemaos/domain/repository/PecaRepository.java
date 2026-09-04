package br.com.sistemaos.domain.repository;

import br.com.sistemaos.domain.entity.Peca;
import br.com.sistemaos.domain.model.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PecaRepository extends JpaRepository<Peca, Long> {
    Optional<Peca> findByNome(String nome);

    @Query("""
    SELECT p
    FROM Peca p
    WHERE (:nome IS NULL OR LOWER(p.nome) LIKE LOWER(CONCAT('%', :nome, '%')))
      AND (:status IS NULL OR p.status in :status)
    """)
    Page<Peca> listarPecas(@Param("nome") String nome, @Param("status") List<Status> status, Pageable pageable);

    @Query("""
    select p
    from Peca p
    where (:nome is null or LOWER(p.nome) like LOWER(CONCAT('%', :nome, '%')))
    and (:status is null or p.status in :status)
    """)
    Page<Peca> listaPecasByNome(@Param("nome") String nome, @Param("status") Status status, Pageable pageable);
}
