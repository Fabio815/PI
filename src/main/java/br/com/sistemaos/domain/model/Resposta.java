package br.com.sistemaos.domain.model;

import lombok.Data;

@Data
public class Resposta {
    private boolean sucesso;
    private String mensagem;

    public static Resposta sucesso() {
        Resposta resposta = new Resposta();
        resposta.sucesso = true;
        resposta.mensagem = "Cliente cadastro com sucesso!";
        return resposta;
    }

    public static Resposta falha() {
        Resposta resposta = new Resposta();
        resposta.sucesso = true;
        resposta.mensagem = "Cliente não cadastrado!";
        return resposta;
    }
}
