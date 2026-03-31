Ext.define('ProjSistemaOs.model.Estoque', {
    extend: 'Ext.data.Model',

    idProperty: 'id',

    fields: [
        { name: 'id', type: 'int' },
        { name: 'nome', type: 'string' },
        { name: 'qtd', type: 'int' },
        { name: 'valor_unitario', type: 'number' },
        { name: 'statusPeca', type: 'boolean' },
    ]
});