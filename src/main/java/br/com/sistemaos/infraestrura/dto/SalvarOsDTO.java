package br.com.sistemaos.infraestrura.dto;

import br.com.sistemaos.domain.entity.Cliente;
import br.com.sistemaos.domain.entity.Orcamento;
import br.com.sistemaos.domain.entity.Usuario;
import br.com.sistemaos.domain.model.StatusOs;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class SalvarOsDTO {

    private final StatusOs status;
    private final Usuario usuario;
    private final Cliente cliente;
    private final Orcamento orcamento;

    public Cliente getCliente() {
        return cliente;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public StatusOs getStatus() {
        return status;
    }

    public Orcamento getOrcamento() {
        return orcamento;
    }
}