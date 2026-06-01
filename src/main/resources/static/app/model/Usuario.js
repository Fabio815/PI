Ext.define('ProjSistemaOs.model.Usuario', {
    extend: 'Ext.data.Model',

    idProperty: 'id',

    fields: [
        { name: 'id', type: 'int' },
        { name: 'chave', type: 'string' },
        { name: 'nome', type: 'string' },
        { name: 'email', type: 'string' },
        { name: 'ativo', type: 'boolean' }
    ]
});