package br.com.sistemaos.repository;

import br.com.sistemaos.domain.entity.Usuario;
import br.com.sistemaos.domain.model.Status;
import br.com.sistemaos.dto.UsuarioDTO;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByEmail(String email);

    @Transactional
    @Modifying
    @Query("update Usuario as u set u.status=:status where u.id=:id")
    void updateStatus(@Param("status") Status status, @Param("id") Long id);

    @Transactional
    @Modifying
    @Query ("update Usuario as u set u.email=:email, u.nome=:nome where id=:id")
    void updateUsuario(@Param("email") String email, @Param("nome") String nome, @Param("id") Long id);
}