package br.com.sistemaos.domain.repository;

import br.com.sistemaos.domain.entity.HistoricoOs;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HistoricoService extends JpaRepository<HistoricoOs, Long> {
}
