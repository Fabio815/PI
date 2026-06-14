Ext.define('ProjSistemaOs.util.MensagemUtil', {
    alternateClassName: 'Avisos',
    singleton: true,

    /*requires: [
        'Ext.window.MessageBox'
    ],*/

    mensagemSucesso: function(msg) {
        Ext.Msg.alert('Sucesso', msg);
    },

    mensagemAviso: function(msg) {
        Ext.Msg.alert('Aviso', msg);
    },

    mostrarServidorIndisponivel: function() {
        Ext.Msg.alert(
            'Erro',
            'Servidor indisponível. Tente novamente mais tarde.'
        );
    },

    contateAdm: function() {
        Ext.Msg.alert(
            'Aviso',
            'Contate o administrador do sistema.'
        );
    }
});