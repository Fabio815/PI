Ext.define('ProjSistemaOs.util.SessaoUtil', {
    alternateClassName: 'SessaoUtil',
    singleton: true,

    usuarioLogado: null,

    definirUsuarioLogado: function(usuario) {
        this.usuarioLogado = usuario;
        localStorage.setItem('usuarioLogado', JSON.stringify(usuario));
    },

    obterUsuarioLogado: function() {
        if (this.usuarioLogado) {
            return this.usuarioLogado;
        }
        var stored = localStorage.getItem('usuarioLogado');
        if (stored) {
            this.usuarioLogado = JSON.parse(stored);
            return this.usuarioLogado;
        }
        return null;
    },

    obterIdUsuarioLogado: function() {
        var usuario = this.obterUsuarioLogado();
        return usuario ? usuario.id : null;
    },

    obterPerfilUsuarioLogado: function() {
        var usuario = this.obterUsuarioLogado();
        return usuario ? usuario.chave : null;
    },

    ehAdministrador: function() {
        return this.obterPerfilUsuarioLogado() === 'ADM';
    },

    ehFuncionario: function() {
        return this.obterPerfilUsuarioLogado() === 'FUNCIONARIO';
    },

    podeEditarOs: function(usuarioCriador) {
        var usuarioLogado = this.obterUsuarioLogado();
        if (!usuarioLogado) {
            return false;
        }
        return usuarioLogado.id === usuarioCriador || this.ehAdministrador();
    },

    limparSessao: function() {
        this.usuarioLogado = null;
        localStorage.removeItem('usuarioLogado');
    }
});