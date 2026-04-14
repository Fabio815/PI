package br.com.sistemaos.dto;

import br.com.sistemaos.domain.entity.Endereco;
import br.com.sistemaos.domain.entity.Os;
import br.com.sistemaos.domain.model.Status;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ClienteDTO {
    private Long id;
    private String nome;
    private String telefone;
    private Status status;
    private EnderecoDTO endereco;
    //private List<Os> ordensServico;
}