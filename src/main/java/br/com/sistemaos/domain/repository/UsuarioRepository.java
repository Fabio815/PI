package br.com.sistemaos.domain.repository;

import br.com.sistemaos.domain.entity.Usuario;
import br.com.sistemaos.domain.model.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByEmail(String email);
    Optional<Usuario> findByResetToken(String resetToken);

    @Query("""
    select u
    from Usuario u
    where (:nome is null or lower(u.nome) like lower(concat('%', :nome, '%')))
    and (:email is null or lower(u.email) like lower(concat('%', :email, '%')))
    and (:status is null or u.status in :status)
    and (:id is null or u.id = :id)
    """)
    Page<Usuario> listarUsuarios(
            @Param("nome") String nome,
            @Param("email")  String email,
            @Param("status") List<Status> status,
            @Param("id") Long id,
            Pageable pageable
    );
}