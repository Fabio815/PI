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

    public List<Usuario> listagemUsuarios(List<Filtro> filtros) {
        List<Usuario> listUsuarios = new ArrayList<>();
        StringBuilder sql = new StringBuilder();
        sql.append("""
                    select u from Usuario as u
                """);
        String temporarioSQL = " where ";
        for (Filtro f : filtros) {
            switch(f.getOperador()) {
                case "eq":
                    sql.append(temporarioSQL).append(" u.id = :id ");
                    temporarioSQL = " and ";
                    break;
                case "like":
                    if (f.getPropriedade().equals("nome")) {
                        sql.append(temporarioSQL).append(" u.nome like :nome ");
                    } else  {
                        sql.append(temporarioSQL).append(" u.email like :email ");
                    }
                    temporarioSQL = " and ";
                    break;
                case "in":
                    sql.append(temporarioSQL).append(" u.status = :status ");
                    temporarioSQL = " and ";
                    break;
                default:
                    break;
            }
        }

        TypedQuery<Usuario> query = manager.createQuery(sql.toString(), Usuario.class);

        for (Filtro f : filtros) {
            switch(f.getOperador()) {
                case "eq":
                        query.setParameter("id", f.getValor());
                    break;
                case "like":
                    if (f.getPropriedade().equals("nome")) {
                        query.setParameter("nome", "%" + f.getValor() + "%");
                    } else  {
                        query.setParameter("email", "%" + f.getValor() + "%");
                    }
                    break;
                case "in":
                    query.setParameter("status", f.getValor());
                    break;
                default:
                    break;
            }
        }

        listUsuarios = query.getResultList();
        return listUsuarios;
    }
}
