Ext.define('ProjSistemaOs.model.Cliente', {
    extend: 'Ext.data.Model',

    idProperty: 'id',

    fields: [
        { name: 'id', type: 'int' },
        { name: 'nome', type: 'string' },
        { name: 'telefone', type: 'string' },
        { name: 'rua', type: 'string' },
        { name: 'bairro', type: 'string' },
        { name: 'numero', type: 'string' },
        { name: 'complemento', type: 'string' },
        { name: 'statusCliente', type: 'boolean' }
    ]
});