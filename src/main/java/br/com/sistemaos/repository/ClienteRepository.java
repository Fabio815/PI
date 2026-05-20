package br.com.sistemaos.repository;

import br.com.sistemaos.domain.entity.Cliente;
import br.com.sistemaos.domain.model.Status;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository //Ele vai armazenar o crud

public interface ClienteRepository extends JpaRepository <Cliente, Long>, JpaSpecificationExecutor<Cliente> {
    @Transactional
    @Modifying
    @Query("update Cliente as c set c.nome=:nome, c.telefone=:telefone where c.id=:id")
    void updateCliente(@Param("nome") String nome,
                       @Param("telefone") String telefone,
                       @Param("id") Long id);

    @Transactional
    @Modifying
    @Query("update Cliente as c set c.status=:status where c.id=:id")
    void udpateStatus(@Param("status") Status status, Long id);


    //Isso é uma query, onde o Long id é parametro que será passado para buscar o cliente
    //@Query("from Cliente as c where c.id=:id")
    //Cliente buscarClientePorId(Long id);

    //Também consigo buscar por mais de 1 filtro, devido ao And
    //List<Cliente> findByIdAndTelefone(Long id, Long telefone);
}
