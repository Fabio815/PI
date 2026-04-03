package br.com.sistemaos.repository;

import br.com.sistemaos.domain.entity.Cliente;
import br.com.sistemaos.dto.ClienteRespostaDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository //Ele vai armazenar o crud

public interface ClienteRepository extends JpaRepository <Cliente, Long> {
}
