package br.com.sistemaos.domain.repository;

import br.com.sistemaos.domain.entity.Cliente;
import br.com.sistemaos.domain.model.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository //Ele vai armazenar o crud

public interface ClienteRepository extends JpaRepository <Cliente, Long>, JpaSpecificationExecutor<Cliente> {
    @Query("""
    select c
    from Cliente c
    where (:nome is null or lower(c.nome) like lower(concat('%', :nome, '%')))
      and (:status is null or c.status in :status)
      and (:id is null or c.id = :id)
    """)
    Page<Cliente> listarClientes(
            @Param("nome") String nome,
            @Param("status") List<Status> status,
            @Param("id") Long id,
            Pageable pageable
    );

    @Query("select c from Cliente c where c.status=ATIVO")
    Page<Cliente> findAllNotInativo(Pageable pageable);

    //Isso é uma query, onde o Long id é parametro que será passado para buscar o cliente
    //@Query("from Cliente as c where c.id=:id")
    //Cliente buscarClientePorId(Long id);

    //Também consigo buscar por mais de 1 filtro, devido ao And
    //List<Cliente> findByIdAndTelefone(Long id, Long telefone);
}
