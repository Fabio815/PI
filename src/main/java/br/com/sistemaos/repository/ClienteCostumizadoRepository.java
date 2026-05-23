package br.com.sistemaos.repository;

import br.com.sistemaos.domain.entity.Cliente;
import br.com.sistemaos.domain.model.Filtro;
import br.com.sistemaos.domain.model.Status;
import jakarta.persistence.EntityManager;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Repository;

import java.beans.ConstructorProperties;
import java.util.List;

@Repository
@AllArgsConstructor
public class ClienteCostumizadoRepository {
    private final EntityManager manager;

    public List<Cliente> listagemClientes(List<Filtro> filtro) {
        StringBuilder sql = new StringBuilder("""
                        select c from Cliente as c
                        left join c.endereco e
                """);
        String condicao = "where";

        for (Filtro f : filtro) {
            switch (f.getOperador()) {
                case "eq":
                    sql.append(condicao);
                    sql.append(" c.id = :id ");
                    condicao = "and";
                    break;
                case "like":
                    sql.append(condicao);
                    sql.append(" c.nome like :nome ");
                    condicao = "and";
                    break;
                case "in":
                    sql.append(condicao);
                    sql.append(" c.status = :status ");
                    condicao = "and";
                    break;
            }
        }

        var parametros = manager.createQuery(sql.toString(), Cliente.class);
        for (Filtro f : filtro) {
            switch (f.getOperador()) {
                case "eq":
                    parametros.setParameter("id", f.getValor());
                    break;
                case "like":
                    parametros.setParameter("nome", "%" + f.getValor() + "%");
                    break;
                case "in":
                    parametros.setParameter("status", Status.valueOf(f.getValor()));
                    break;
            }
        }
        return parametros.getResultList();
    }
}
