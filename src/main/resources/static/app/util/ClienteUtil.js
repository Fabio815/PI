Ext.define('ProjSistemaOs.util.ClienteUtil', {
    alternateClassName: 'formataDadosCliente',
    singleton: true,

    converteEstrutura: function (record, status) {
        return {
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