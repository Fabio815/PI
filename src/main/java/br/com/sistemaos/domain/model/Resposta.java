package br.com.sistemaos.domain.model;

import lombok.Data;

@Data
public class Resposta {
    private boolean sucesso;
    private String mensagem;

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
