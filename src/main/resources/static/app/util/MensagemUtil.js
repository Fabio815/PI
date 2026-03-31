Ext.define('ProjSistemaOs.util.MensagemUtil', {
    alternateClassName: 'Avisos',
    singleton: true, //É uma classe singleton, ou seja, não preciso ficar instanciando toda hora :)

    mensagemSucesso: function(msg) {
        Ext.toast(msg, '<i class="fa fa-check"></i> Aviso', 't');
    },
    mensagemAviso: function(msg) {
        Ext.toast(msg, '<i class="fa fa-bell"></i> Aviso', 't');
    },
    mostrarServidorIndisponivel: function() {
        Ext.toast('Servidor indisponível', '<i class="fa fa-bell"></i> Aviso', 't');
    }
});