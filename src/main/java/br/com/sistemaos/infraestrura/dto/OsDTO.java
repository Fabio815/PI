package br.com.sistemaos.infraestrura.dto;

import br.com.sistemaos.domain.entity.Cliente;
import br.com.sistemaos.domain.entity.Os;
import br.com.sistemaos.domain.entity.Usuario;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OsDTO {

    private Usuario usuario;
    private Cliente cliente;
    private String status;

    public static OsDTO criar(Os os) {

        OsDTO dto = new OsDTO();

        dto.setUsuario(os.getUsuario());
        dto.setCliente(os.getCliente());
        dto.setStatus(os.getStatus().toString());

        return dto;
    }
}