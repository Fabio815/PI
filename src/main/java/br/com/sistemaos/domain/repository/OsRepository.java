package br.com.sistemaos.domain.repository;

import br.com.sistemaos.domain.entity.Os;
import br.com.sistemaos.domain.model.Status;
import br.com.sistemaos.infraestrura.dto.OsListagemDTO;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OsRepository extends JpaRepository<Os, Long> {
    @Query("""
    select new br.com.sistemaos.infraestrura.dto.OsListagemDTO(
        os.id, os.dataEmissao, os.cliente.nome, os.cliente.telefone,
        os.orcamento.valorTotal, os.situacao, os.status, criador.id, criador.nome,
        coalesce(resp.id, criador.id), coalesce(resp.nome, criador.nome), os.dataFim
    )
    from Os os
    left join os.responsavel resp
    left join os.usuario criador
    where (:id is null or os.id = :id)
    and (:nome is null or lower(os.cliente.nome) like lower(concat('%', :nome, '%')))
    and (:status is null or os.status in :status)
    order by os.id asc
    """)
    Page<OsListagemDTO> listarOs(@Param("id") Long id, @Param("nome") String nome,
                                 @Param("status") List<Status> status, Pageable pageable);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select os from Os os where os.id = :id")
    Optional<Os> findByIdForUpdate(@Param("id") Long id);
}
