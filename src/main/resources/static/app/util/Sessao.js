Ext.define('ProjSistemaOs.util.Sessao', {
    alternateClassName: 'Sessao',
    singleton: true,

    usuarioId: null,
    usuarioNome: null,
    perfil: null,

    definirUsuario: function (id, nome, perfil) {
        this.usuarioId = id;
        this.usuarioNome = nome;
        this.perfil = perfil;
    },

    limpar: function () {
        this.usuarioId = null;
        this.usuarioNome = null;
        this.perfil = null;
    },

    isAdm: function () {
        return this.perfil === 'ADM';
    },

    // true se o usuário logado pode alterar essa OS (ADM sempre pode; funcionário só a que ele criou)
    podeEditarOs: function (usuarioIdDaOs) {
        return this.isAdm() || this.usuarioId === usuarioIdDaOs;
    }
});
