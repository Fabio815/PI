package br.com.sistemaos.repository;

import br.com.sistemaos.domain.entity.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository //Ele vai armazenar o crud

public interface ClienteRepository extends JpaRepository <Cliente, Long> {

}
