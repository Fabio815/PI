Ext.define('ProjSistemaOs.model.Cliente', {
    extend: 'Ext.data.Model',

    idProperty: 'id',

    fields: [
        { name: 'id', type: 'int' },
        { name: 'nome', type: 'string' },
        { name: 'telefone', type: 'string' },
        { name: 'statusCliente', type: 'boolean' }
    ],
    hasOne: {
        model: 'ProjSistemaOs.model.Endereco',
        name: 'endereco',
    }
});