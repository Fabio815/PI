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
        dataIndex: 'valor_unitario',
        flex: 2,
        renderer: function(value) {
            if (Ext.isNumber(value)) {
                return new Intl.NumberFormat('pt-BR', {
                    style: 'currency',
                    currency: 'BRL'
                }).format(value);
            }
            return value;
        }
    }, {
        xtype: 'checkcolumn',
        text: 'Ativo',
        dataIndex: 'ativo',
        width: 70
    }]
});
