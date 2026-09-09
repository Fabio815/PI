Ext.define('ProjSistemaOs.model.Os', {
    extend: 'Ext.data.Model',

    idProperty: 'id',

    fields: [
        { name: 'id', type: 'int' },
        { name: 'dataEmissao', type: 'date' },
        { name: 'nomeCliente', type: 'string' },
        { name: 'telefone', type: 'string' },
        { name: 'valorTotal', type: 'float'},
        { name: 'situacao', type: 'string' },
        { name: 'status', type: 'string' },
        { name: '_status', type: 'string' }
    ]
});