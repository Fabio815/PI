Ext.define('ProjSistemaOs.view.cliente.ClientesGrid', {
    extend: 'Ext.grid.Panel',
    xtype: 'clienteGrid',

    requires: [
        'Ext.grid.column.Check',
        'Ext.grid.plugin.CellEditing',
        'Ext.grid.filters.Filters',
        'Ext.toolbar.Paging',

        'ProjSistemaOs.store.Cliente',
        'ProjSistemaOs.view.cliente.ClienteWindow',
        'ProjSistemaOs.util.MensagemUtil'
    ],

    controller: {
        adicionarCliente: function(){
            var me = this, vw = me.getViewModel(),
                win = Ext.create('ProjSistemaOs.view.cliente.ClienteWindow');

            win.on('clienteSalvo', () => { //Aqui está escutando o envento que é disparadp quando salva o cliente.
                if (vw && !vw.destroyed && !vw.isDestroying) {
                    me.getView().getStore().load();
                }
            });
            win.show();
        },
        recarregarGrid: function () {
            var me = this, vw = me.getView();
            if (vw && !vw.destroyed && !vw.isDestroying) {
                me.getView().getStore().load();
            }
        },
        atualizarCliente: function (edit, context) {
            var me = this, vw = me.getViewModel();
            var record = context.record,
                oldValue = context.originalValue,
                newValue = context.value;
            console.log(record);
            record.data.statusCliente = record.data.statusCliente ? 1 : 2;
            if (oldValue != newValue) {
                Ext.Ajax.request({
                    url: 'http://localhost:8080/sistema-os/api/cliente/atualizar',
                    method: 'POST',
                    jsonData: record.getData(),
                    success: function (response) {
                        var r = Ext.decode(response.responseText, true);
                        if (r && r.sucesso) {
                            record.commit();
                            Avisos.mensagemSucesso(r.mensagem);
                        } else if (r) {
                            record.reject();
                            Avisos.mensagemAviso(r.mensagem);
                        } else {
                            Avisos.mostrarServidorIndisponivel();
                        }
                    },
                    failure: function(response) {
                        var r = Ext.decode(response.responseText, true);
                        record.reject();
                        if (r && r.mensagem) {
                           Avisos.mensagemAviso(r.mensagem);
                        } else {
                           Avisos.mostrarServidorIndisponivel();
                        }
                    }
                })
            }
        }
    },

    enableColumnHide: false,

    title: 'Clientes',
    layout: 'fit',

    store: {
        type: 'cliente-listagem-store'
    },

	tbar: [{
		xtype: 'button',
		tooltip: 'Adicionar',
		iconCls: 'fa fa-plus',
		width: 40,
		height: 40,
		handler: 'adicionarCliente'
	}, {
        xtype: 'button',
        tooltip: 'Recarregar',
        iconCls: 'fa fa-sync',
        width: 40,
        height: 40,
        handler: 'recarregarGrid'
    }],
    columns: [{
        text: 'Id',
        itemId: 'id',
        dataIndex: 'id',
        width: 60,
        filter: {
            type: 'number',
            menuItems: ['eq']
        }
    }, {
        text: 'Nome',
        imtemId: 'nome',
        dataIndex: 'nome',
        width: 220,
        filter: 'string',
        editor: {
            type: 'textfield',
            allowBlank: false,
            blankText: 'Este campo é obrigatório',
        }
    }, {
        text: 'Telefone',
        itemId: 'telefone',
        dataIndex: 'telefone',
        flex: 1,
        sortable: false,
        editor: {
            type: 'textfield',
            allowBlank: false,
            blankText: 'Este campo é obrigatório',
        }
    }, {
        text: 'Rua',
        itemId: 'rua',
        dataIndex: 'rua',
        flex: 2,
        sortable: false,
        editor: {
            type: 'textfield'
        }
    }, {
        text: 'Logradouro',
        itemId: 'logradouro',
        dataIndex: 'logradouro',
        flex: 1,
        sortable: false,
        editor: {
            type: 'textfield'
        }
    }, {
        text: 'Número',
        itemId: 'nomero',
        dataIndex: 'numero',
        width: 100,
        sortable: false,
        editor: {
            type: 'numberfield',
        }
    }, {
        text: 'Complemento',
        itemId: 'complemento',
        dataIndex: 'complemento',
        flex: 2,
        sortable: false,
        editor: {
            type: 'textfield'
        }
    }, {
        xtype: 'checkcolumn',
        itemId: 'checkcolumn',
        text: 'Ativo',
        dataIndex: 'status',
        width: 80,
        filter: {
            type: 'boolean',
            yes: 'true',
            no: 'false',
            yesText: 'Sim',
            noText: 'Não',
            default: true
        }
    }],
    plugins: {
        gridfilters: true,
        cellediting: {
            clicksToEdit: 2,
            listeners: {
                edit: 'atualizarCliente'
            }
        }
    },
    bbar: {
        xtype: 'pagingtoolbar',
        pageSize: 10,
        displayInfo: true,
        beforePageText: 'Página',
        afterPageText: 'de {0}',
        displayMsg: 'Clientes {0} - {1} de {2}',
        emptyMsg: 'Não existe clientes cadastrados',
        store: this.store,
        listeners: { //Para esconder o botão de reload...
            afterrender: function(toolbar) {
                toolbar.down('#refresh').hide();//Buscando pelo itemId
            }
        }
    }
});