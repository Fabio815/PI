package br.com.sistemaos.domain.repository;

import br.com.sistemaos.domain.entity.HistoricoOs;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HistoricoOsRepository extends JpaRepository<HistoricoOs, Long> {
    List<HistoricoOs> findByOsIdOrderByDataAlteracaoDescIdDesc(Long osId);

    /** Compatível com chamadas externas anteriores. */
    List<HistoricoOs> findByOsIdOrderByDataAlteracaoDesc(Long osId);
}
