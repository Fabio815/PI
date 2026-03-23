package br.com.sistemaos.dto;

import br.com.sistemaos.domain.entity.Endereco;
import br.com.sistemaos.domain.entity.Os;
import br.com.sistemaos.domain.model.Status;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
public class ClienteDTO {

    private Long id;

    private String nome;

    private String telefone;

    private Status status;

    private Endereco endereco;

    private List<Os> ordensServico;
}