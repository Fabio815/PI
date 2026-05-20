package br.com.sistemaos.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Resposta {
    private boolean sucesso;
    private String mensagem;

    public Resposta(Resposta resposta) {
        this.sucesso = resposta.sucesso;
        this.mensagem = resposta.mensagem;
    }

    public static Resposta sucesso(String mensagem) {
        Resposta resposta = new Resposta();
        resposta.sucesso = true;
        resposta.mensagem = mensagem;
        return resposta;
    }

    public static Resposta falha(String mensagem) {
        Resposta resposta = new Resposta();
        resposta.sucesso = false;
        resposta.mensagem = mensagem;
        return resposta;
    }
}
