Ext.define('ProjSistemaOs.model.Os', {
    extend: 'Ext.data.Model',

    idProperty: 'id',

    fields: [
        { name: 'id', type: 'int' },
        { name: 'dataInicio', type: 'date' },
        { name: 'nomeCliente', type: 'string' },
        { name: 'telefone', type: 'string' },
        { name: 'preco', type: 'float'},
        { name: 'situacao', type: 'string' },
        { name: 'dataFim', type: 'date' },
        { name: 'status', type: 'string' },
        { name: '_status', type: 'string' },
    ]
});