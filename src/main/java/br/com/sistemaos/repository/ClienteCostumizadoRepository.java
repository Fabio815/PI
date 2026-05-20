package br.com.sistemaos.repository;

import br.com.sistemaos.domain.entity.Cliente;
import jakarta.persistence.EntityManager;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

import java.beans.ConstructorProperties;
import java.util.List;

@Repository
@AllArgsConstructor
public class ClienteCostumizadoRepository {
    private final EntityManager manager;

    public List<Cliente> find() {
        StringBuilder sql = new StringBuilder("select c from Cliente c");
        return null;
    }
}
