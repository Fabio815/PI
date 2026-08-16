Ext.define('ProjSistemaOs.model.Estoque', {
    extend: 'Ext.data.Model',

    idProperty: 'id',

    fields: [
        { name: 'id', type: 'int' },
        { name: 'nome', type: 'string' },
        { name: 'quantidade', type: 'int' },
        { name: 'preco', type: 'number' },
        { name: 'status', type: 'boolean' },
        { name: '_status', type: 'string', default: undefined },
        {name: 'status', type: 'string'}
    ]
});