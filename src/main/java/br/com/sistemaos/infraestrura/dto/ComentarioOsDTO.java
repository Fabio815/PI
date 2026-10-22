package br.com.sistemaos.infraestrura.dto;

import br.com.sistemaos.domain.entity.ComentarioOs;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
public class ComentarioOsDTO {
    private Long id;
    private String conteudo;
    private UsuarioRefDTO autor;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime dataCriacao;
    private UsuarioRefDTO editadoPor;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime dataEdicao;

    public static ComentarioOsDTO criar(ComentarioOs comentario) {
        return new ComentarioOsDTO(comentario.getId(), comentario.getConteudo(),
                UsuarioRefDTO.criar(comentario.getAutor()), comentario.getDataCriacao(),
                UsuarioRefDTO.criar(comentario.getEditadoPor()), comentario.getDataEdicao());
    }
}
