package br.com.sistemaos.domain.repository;

@Repository //Ele vai armazenar o crud

public interface OsRepository extends JpaRepository<Os, Long> {
}
