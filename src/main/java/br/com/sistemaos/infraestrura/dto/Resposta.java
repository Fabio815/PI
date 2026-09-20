package br.com.sistemaos.infraestrura.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Resposta {
    private final boolean sucesso;
    private final String mensagem;
}
