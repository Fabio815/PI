package br.com.sistemaos.repository;

import br.com.sistemaos.domain.entity.Cliente;
import br.com.sistemaos.domain.model.Status;
import br.com.sistemaos.dto.ClienteRespostaDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository //Ele vai armazenar o crud

public interface ClienteRepository extends JpaRepository <Cliente, Long> {
    List<Cliente> findByNomeContainingIgnoreCase(String nome);

    Page<Cliente> findByNomeContainingIgnoreCase(String nome, Pageable pageable);
    Page<Cliente> findById(Long id, Pageable pageable);
    Page<Cliente> findByStatus(Status status, Pageable pageable);
}
