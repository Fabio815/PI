Ext.define('ProjSistemaOs.model.Endereco', {
    extend: 'Ext.data.Model',
    idProperty: 'id',

    fields: [
        { name: 'id', type: 'int' },
        { name: 'cliente_id', type: 'int' },
        { name: 'rua', type: 'string' },
        { name: 'logradouro', type: 'string' },
        { name: 'numero', type: 'string' },
        { name: 'complemento', type: 'string' }
    ],

    belongsTo: 'Cliente'
});