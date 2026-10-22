package br.com.sistemaos.domain.repository;

import br.com.sistemaos.domain.entity.ComentarioOs;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ComentarioOsRepository extends JpaRepository<ComentarioOs, Long> {
    List<ComentarioOs> findByOsIdOrderByDataCriacaoAscIdAsc(Long osId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select c from ComentarioOs c where c.id = :id and c.os.id = :osId")
    Optional<ComentarioOs> findByIdAndOsIdForUpdate(@Param("id") Long id, @Param("osId") Long osId);
}
