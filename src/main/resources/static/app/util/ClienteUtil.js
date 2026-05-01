Ext.define('ProjSistemaOs.util.MensagemUtil', {
    alternateClassName: 'formataDadosCliente',
    singleton: true,

    converteEstrtura: function (record, status) {
        return dados = {
            nome: record.nome,
            telefone: record.telefone,
            status: status,
            endereco: {
                rua: record.rua,
                numero: record.numero,
                logradouro: record.logradouro,
                complemento: record.complemento
            }
        }
    }
});