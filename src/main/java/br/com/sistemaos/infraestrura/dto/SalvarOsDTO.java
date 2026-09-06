package br.com.sistemaos.infraestrura.dto;

import br.com.sistemaos.domain.entity.Cliente;
import br.com.sistemaos.domain.entity.Orcamento;
import br.com.sistemaos.domain.entity.Usuario;
import br.com.sistemaos.domain.model.StatusOs;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Persistable;

@AllArgsConstructor
public class SalvarOsDTO {
    private final StatusOs status;
    private final Usuario usuario;
    private final Cliente cliente;
    private final Orcamento orcamento;

    public Persistable<java.lang.Object> getCliente() {
    }

    public Persistable<java.lang.Object> getUsuario() {
    }

    public StatusOs getStatus() {
        return null;
    }
}
