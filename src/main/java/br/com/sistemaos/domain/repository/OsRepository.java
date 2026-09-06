package br.com.sistemaos.domain.repository;

import br.com.sistemaos.domain.entity.Os;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository //Ele vai armazenar o crud
public interface OsRepository extends JpaRepository<Os, Long> {
}
