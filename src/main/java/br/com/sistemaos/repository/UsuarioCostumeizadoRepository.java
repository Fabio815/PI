package br.com.sistemaos.repository;

import br.com.sistemaos.domain.entity.Cliente;
import br.com.sistemaos.domain.entity.Usuario;
import br.com.sistemaos.domain.model.Filtro;
import br.com.sistemaos.domain.model.Status;
import br.com.sistemaos.dto.UsuarioDTO;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.AllArgsConstructor;
import org.apache.catalina.Manager;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@AllArgsConstructor
@Repository
public class UsuarioCostumeizadoRepository {

    private final EntityManager manager;

    public List<Usuario> listagemUsuarios(
            List<Filtro> filtros,
            int start,
            int limit) {

        StringBuilder sql = new StringBuilder("""
                select u from Usuario as u
                """);

        adicionarFiltros(sql, filtros);

        TypedQuery<Usuario> query =
                manager.createQuery(sql.toString(), Usuario.class);

        preencherParametros(query, filtros);

        query.setFirstResult(start);
        query.setMaxResults(limit);

        return query.getResultList();
    }

    public long contarUsuarios(List<Filtro> filtros) {

        StringBuilder sql = new StringBuilder("""
                select count(u) from Usuario as u
                """);

        adicionarFiltros(sql, filtros);

        TypedQuery<Long> query =
                manager.createQuery(sql.toString(), Long.class);

        preencherParametros(query, filtros);

        return query.getSingleResult();
    }

    private void adicionarFiltros(
            StringBuilder sql,
            List<Filtro> filtros) {

        String temporarioSQL = " where ";

        for (Filtro f : filtros) {

            switch (f.getOperador()) {

                case "eq":
                    sql.append(temporarioSQL)
                            .append(" u.id = :id ");
                    temporarioSQL = " and ";
                    break;

                case "like":

                    if (f.getPropriedade().equals("nome")) {
                        sql.append(temporarioSQL)
                                .append(" u.nome like :nome ");
                    } else {
                        sql.append(temporarioSQL)
                                .append(" u.email like :email ");
                    }

                    temporarioSQL = " and ";
                    break;

                case "in":

                    sql.append(temporarioSQL)
                            .append(" u.status in :status ");

                    temporarioSQL = " and ";
                    break;

                default:
                    break;
            }
        }
    }

    private void preencherParametros(
            TypedQuery<?> query,
            List<Filtro> filtros) {

        for (Filtro f : filtros) {

            switch (f.getOperador()) {

                case "eq":
                    query.setParameter("id", f.getValor());
                    break;

                case "like":

                    if (f.getPropriedade().equals("nome")) {

                        query.setParameter(
                                "nome",
                                "%" + f.getValor() + "%"
                        );

                    } else {

                        query.setParameter(
                                "email",
                                "%" + f.getValor() + "%"
                        );
                    }

                    break;

                case "in":

                    List<Status> status = new ArrayList<>();

                    List<?> valores = (List<?>) f.getValor();

                    for (Object valor : valores) {
                        status.add(
                                Status.valueOf(valor.toString())
                        );
                    }

                    query.setParameter("status", status);

                    break;

                default:
                    break;
            }
        }
    }
}
