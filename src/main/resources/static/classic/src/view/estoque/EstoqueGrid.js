Ext.define('ProjSistemaOs.view.estoque.EstoqueGrid', {
    extend: 'Ext.grid.Panel',
    xtype: 'estoqueGrid',
    requires: [
        'Ext.grid.column.Check',
        'ProjSistemaOs.store.Cliente'
    ],
    store: {
        type: 'estoque-listagem-store'
    },
    title: 'Cadastro Peças',
    layout: 'fit',

    columns: [{
        text: 'Id',
        dataIndex: 'id',
        width: 50
    }, {
        text: 'Nome da peça',
        dataIndex: 'nome',
        flex: 5
    }, {
        text: 'Quantidade',
        dataIndex: 'qtd',
        flex: 1
    }, {
        text: 'Preço',
        dataIndex: 'valor_unitario'
    }, {
        xtype: 'checkcolumn',
        text: 'Ativo',
        width: 70
    }]
});