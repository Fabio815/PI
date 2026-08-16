Ext.define('ProjSistemaOs.view.estoque.EstoqueGrid', {
    extend: 'Ext.grid.Panel',
    xtype: 'estoqueGrid',
    requires: [
        'ProjSistemaOs.store.Cliente',
        'Ext.grid.column.Action',
        'Ext.grid.column.Check',
        'Ext.grid.plugin.CellEditing',
        'Ext.grid.filters.Filters',
        'Ext.toolbar.Paging',

        'ProjSistemaOs.util.MensagemUtil',
        'ProjSistemaOs.util.Config'
    ],

    controller: {
        recarregarGrid: function () {
            var me = this, vw = me.getView();
            if (me.getView() && !me.getView().destroyed) {
                me.getView().getStore().reload();
            }
        },
        limparPesquisa: function (e, t, eOpts) {
            let a = e.up('grid');
            if (a) {
                a.filters.clearFilters();
                a.getStore().getSorters().removeAll();
            }
        }
    },

    store: {
        type: 'estoque-listagem-store'
    },
    title: 'Cadastro Peças',
    layout: 'fit',

    tbar: [{
        xtype: 'button',
        tooltip: 'Adicionar',
        iconCls: 'fa fa-plus',
        handler: 'adicionarProduto'
    }, '-', {
        xtype: 'button',
        tooltip: 'Recarregar',
        iconCls: 'fa fa-sync',
        handler: 'recarregarGrid'
    }, '->', {
        xtype: "button",
        iconCls: "fas fa-ban",
        tooltip: "Limpar Pesquisa",
        listeners: {
            click: "limparPesquisa"
        }
    }],
    enableColumnHide: false,

    columns: [{
        text: 'Id',
        dataIndex: 'id',
        width: 50,
        filter: false,
        sortable: false,
    }, {
        text: 'Nome da peça',
        dataIndex: 'nome',
        flex: 5,
        sortable: false,
        filter: 'string',
        editor: {
            type: 'textfield',
            allowBlank: false,
            blankText: 'Este campo é obrigatório',
        }
    }, {
        text: 'Quantidade',
        dataIndex: 'quantidade',
        flex: 1,
        editor: {
            type: 'textfield',
            allowBlank: false,
            blankText: 'Este campo é obrigatório',
        }
    }, {
        text: 'Preço',
        dataIndex: 'preco',
        flex: 2,
        renderer: function(value) {
            if (Ext.isNumber(value)) {
                return new Intl.NumberFormat('pt-BR', {
                    style: 'currency',
                    currency: 'BRL'
                }).format(value);
            }
            return value;
        },
        sortable: true,
        editor: {
            type: 'textfield',
            allowBlank: false,
            blankText: 'Este campo é obrigatório',
        }
    }, {
        xtype: 'actioncolumn',
        dataIndex: 'status',
        itemId: 'status',
        width: 75,
        text: 'Ativo',
        align: 'center',
        editable: false,
        items: [{
            getClass: function (v, meta, record) {
                if (record.get('status') && record.get('_status')) {
                    switch (record.get('_status')) {
                        case 'ATIVO':
                            return 'far fa-square red';
                        case 'INATIVO':
                            return 'far fa-check-square green';
                    }
                } else {
                    switch (record.get('status')) {
                        case 'ATIVO':
                            return 'far fa-check-square';
                        case 'INATIVO':
                            return 'far fa-square';
                    }
                }
            },
            getTip: function(v, meta, record) {
                if (record.get('status') && record.get('_status')) {
                    switch (record.get('_status')) {
                        case 'ATIVO':
                            return 'Realmente inativar?';
                        case 'INATIVO':
                            return 'Realmente ativar?';
                    }
                } else {
                    switch (record.get('status')) {
                        case 'ATIVO':
                            return 'Inativar';
                        case 'INATIVO':
                            return 'Ativar';
                    }
                }
            },
            handler: function(a, b, e, f, h, record, k) {
                if (record.get('_status') === 'ATIVO' || record.get('_status') === 'INATIVO') {
                    this.fireEvent("trocarStatus", a, b, e, f, h, record, k);
                    //console.log('Entrou no if do evento');
                } else {
                    record.set('_status', record.get('status'));
                }
            }
        }],
        filter: {
            type: 'list',
            options: [['ATIVO', 'Sim'], ['INATIVO', 'Não']],
            value: 'ATIVO'
        }
    }],

    plugins: {
        gridfilters: true,
        cellediting: {
            clicksToEdit: 2,
            listeners: {
                edit: 'atualizarProduto'
            }
        }
    },
    bbar: {
        xtype: 'pagingtoolbar',
        pageSize: 10,
        displayInfo: true,
        beforePageText: 'Página',
        afterPageText: 'de {0}',
        displayMsg: 'Peças {0} - {1} de {2}',
        emptyMsg: 'Não existe peças cadastrados',
        bind: {
            store: '{estoque-listagem-store}'
        },
        listeners: { //Para esconder o botão de reload...
            afterrender: function(toolbar) {
                toolbar.down('#refresh').hide();//Buscando pelo itemId
            }
        }
    }
});
