package br.com.sistemaos.repository;

import br.com.sistemaos.domain.entity.Cliente;
import br.com.sistemaos.domain.model.Filtro;
import jakarta.persistence.EntityManager;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

import java.beans.ConstructorProperties;
import java.util.List;

@Repository
@AllArgsConstructor
public class ClienteCostumizadoRepository {
    private final EntityManager manager;

    public List<Cliente> find(List<Filtro> filtro) {
        StringBuilder sql = new StringBuilder("""
                        select * from cliente as c
                        left join endereco as e on e.id_cliente=c.id
                """);
        String condicao = "where";
        for (Filtro f : filtro) {
            switch (f.getOperador()) {
                
            }
        }

        return null;
    }
}
