package br.com.sistemaos.domain.repository;

import br.com.sistemaos.domain.entity.Cliente;
import br.com.sistemaos.domain.entity.Os;
import br.com.sistemaos.domain.model.Status;
import br.com.sistemaos.domain.model.StatusOs;
import br.com.sistemaos.infraestrura.dto.OsListagemDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository //Ele vai armazenar o crud
public interface OsRepository extends JpaRepository<Os, Long> {
    @Query("""
    select new br.com.sistemaos.infraestrura.dto.OsListagemDTO(
        o.id, o.dataEmissao, o.cliente.nome, o.cliente.telefone,
        o.orcamento.valorTotal, o.status, o.ativo
    )
    from Os o
    where (:id is null or o.id = :id)
    and (:nome is null or lower(o.cliente.nome) like lower(concat('%', :nome, '%')))
    and (:status is null or o.status = :status)
    and (:ativo is null or o.ativo = :ativo)
    order by o.id desc
    """)
    Page<OsListagemDTO> listarOs(
            @Param("id") Long id,
            @Param("nome") String nome,
            @Param("status") StatusOs status,
            @Param("ativo") Boolean ativo,
            Pageable pageable
    );
}
