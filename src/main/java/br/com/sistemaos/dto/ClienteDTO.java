package br.com.sistemaos.dto;

import br.com.sistemaos.domain.entity.Endereco;
import br.com.sistemaos.domain.entity.Os;
import br.com.sistemaos.domain.model.Status;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
    @NotNull(message = "O nome do cliente não pode ser vazio")
    @Size(min = 1, max = 50, message = "Nome maior que 50 caracteres")
    private String nome;
    @NotNull(message = "O telefone não pode ser vazio")
    @Size(min = 1, max = 15, message = "Telefone maior que 14 caracteres")
    private String telefone;
    private Status status;
    private EnderecoDTO endereco;
    //private List<Os> ordensServico;
}