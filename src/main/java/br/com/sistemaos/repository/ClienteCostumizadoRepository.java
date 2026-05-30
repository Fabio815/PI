package br.com.sistemaos.repository;

import br.com.sistemaos.domain.entity.Cliente;
import br.com.sistemaos.domain.model.Filtro;
import br.com.sistemaos.domain.model.Status;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.beans.ConstructorProperties;
import java.util.ArrayList;
import java.util.List;

@Repository
@AllArgsConstructor
public class ClienteCostumizadoRepository {
    private final EntityManager manager;

    public Page<Cliente> listagemClientes(List<Filtro> filtro, Pageable pageable) {

        StringBuilder sql = new StringBuilder("""
        select c from Cliente c
        left join c.endereco e
    """);

        StringBuilder countSql = new StringBuilder("""
        select count(c) from Cliente c
        left join c.endereco e
    """);

        String condicao = " where ";

        for (Filtro f : filtro) {
            switch (f.getOperador()) {
                case "eq":
                    sql.append(condicao).append(" c.id = :id ");
                    countSql.append(condicao).append(" c.id = :id ");
                    condicao = " and ";
                    break;
                case "like":
                    sql.append(condicao).append(" c.nome like :nome ");
                    countSql.append(condicao).append(" c.nome like :nome ");
                    condicao = " and ";
                    break;

                case "in":
                    sql.append(condicao).append(" c.status in :status ");
                    countSql.append(condicao).append(" c.status in :status ");
                    condicao = " and ";
                    break;
            }
        }

        TypedQuery<Cliente> query = manager.createQuery(sql.toString(), Cliente.class);

        TypedQuery<Long> countQuery = manager.createQuery(countSql.toString(), Long.class);

        for (Filtro f : filtro) {
            switch (f.getOperador()) {
                case "eq":
                    query.setParameter("id", f.getValor());
                    countQuery.setParameter("id", f.getValor());
                    break;
                case "like":
                    query.setParameter("nome", "%" + f.getValor() + "%");
                    countQuery.setParameter("nome", "%" + f.getValor() + "%");
                    break;
                case "in":
                    List<String> statusStrings = (List<String>)  f.getValor();
                    List<Status> status = new ArrayList<>();

                    for (String s : statusStrings) {
                        status.add(Status.valueOf(s));
                    }
                    query.setParameter("status", status);
                    countQuery.setParameter("status", status);
                    break;
            }
        }

        query.setFirstResult((int) pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());

        List<Cliente> clientes = query.getResultList();

        Long total = countQuery.getSingleResult();

        return new PageImpl<>(clientes, pageable, total);
    }
}
