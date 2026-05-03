package br.com.sistemaos.repository;

import br.com.sistemaos.domain.entity.Endereco;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface EnderecoRepository extends JpaRepository<Endereco, Long> {


    @Transactional
    @Modifying
    @Query("update Endereco as e set e.complemento=:complemento, e.logradouro=:logradouro, e.numero=:numero, e.rua=:rua where e.id=:id")
    void updateEndereco(@Param("complemento") String complemento,
                        @Param("logradouro") String logradouro,
                        @Param("numero") String numero,
                        @Param("rua") String rua,
                        @Param("id") Long id);
}
