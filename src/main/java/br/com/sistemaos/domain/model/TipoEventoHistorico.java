package br.com.sistemaos.domain.model;

/** Tipos estáveis de auditoria operacional; linhas antigas permanecem com tipo null. */
public enum TipoEventoHistorico {
    CRIACAO,
    ALTERACAO_STATUS,
    ATRIBUICAO,
    REATRIBUICAO,
    EDICAO_CAMPOS,
    CANCELAMENTO,
    CONCLUSAO,
    CRIACAO_COMENTARIO,
    EDICAO_COMENTARIO,
    REABERTURA
}
