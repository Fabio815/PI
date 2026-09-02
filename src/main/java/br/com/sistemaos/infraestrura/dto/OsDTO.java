package br.com.sistemaos.infraestrura.dto;

import br.com.sistemaos.domain.entity.Cliente;
import br.com.sistemaos.domain.entity.Os;
import br.com.sistemaos.domain.entity.Usuario;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class OsDTO {
    private Usuario usuario;
    private Cliente cliente;
    private String status;

    public static OsDTO criar (Os os){

        return OsDTO.builder()
                .id(os.getId())
                .idCliente(os.getCliente().getId())
                .idUsuario(os.getUsuario().getId())
                .status(os.getStatus().name())
                .build();
    }
}
