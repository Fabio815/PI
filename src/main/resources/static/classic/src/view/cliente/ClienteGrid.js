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
        'ProjSistemaOs.util.MensagemUtil',
        'ProjSistemaOs.util.ClienteUtil'
    ],

    controller: {
        adicionarCliente: function(){
            var me = this, vw = me.getViewModel(),
                win = Ext.create('ProjSistemaOs.view.cliente.ClienteWindow');

            win.on('clienteSalvo', () => { //Aqui está escutando o envento que é disparadp quando salva o cliente.
                if (vw && !vw.destroyed && !vw.isDestroying) {
                    me.getView().getStore().reload();
                }
            });
            win.show();
        },
        recarregarGrid: function () {
            var me = this, vw = me.getView();
            if (me.getView() && !me.getView().destroyed) {
                me.getView().getStore().reload();
            }
        },
        atualizarCliente: function (edit, context) {
            var me = this, vw = me.getViewModel();
            var record = context.record,
                oldValue = context.originalValue,
                newValue = context.value;
            var dadosFormato = ProjSistemaOs.util.ClienteUtil.converteEstrutura(record.getData(), record.data.status ? 'ATIVO' : 'INATIVO');
            if (oldValue !== newValue) {
                Ext.Ajax.request({
                    url: 'http://localhost:8080/cliente/atualizar/' + record.get('id'),
                    method: 'PUT',
                    jsonData: dadosFormato,
                    success: function (response) {
                        var r = Ext.decode(response.responseText, true);
                        console.log(r);
                        if (r && r.resposta.sucesso) {
                            record.commit();
                            Avisos.mensagemSucesso(r.resposta.mensagem);
                        } else if (!r && !r.resposta.sucesso) {
                            record.reject();
                            Avisos.mensagemAviso(r.resposta.mensagem);
                        } else {
                            Avisos.contateAdm();
                        }
                    },
                    failure: function(response) {
                        record.reject();
                        Avisos.mostrarServidorIndisponivel();
                    }
                })
            }
        },
        mudarStatus: function (checkColumn, rowIndex, checked, record) {
            console.log(checked);
            let me = this;
            Ext.Ajax.request({
                url: 'http://localhost:8080/cliente/status/' + record.get('id') + '/' + (checked ? 'ATIVO' : 'INATIVO'),
                method: 'PUT',
                success: function (response) {
                    var r = Ext.decode(response.responseText, true);
                    if (r && r.sucesso) {
                        record.set('status', checked);
                        record.commit();
                        me.getView().getStore().reload();
                    } else if (!r && !r.sucesso) {
                        Avisos.mensagemAviso(r.mensagem);
                        record.reject();
                    } else {
                        Avisos.contateAdm();
                    }
                },
                failure: function(response) {
                    record.reject();
                    Avisos.mostrarServidorIndisponivel();
                }
            })
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
        text: '<span style="color:#00a79a;">Id</span>',
        itemId: 'id',
        dataIndex: 'id',
        width: 60,
        filter: {
            type: 'number',
            menuItems: ['eq']
        }
    }, {
        text: '<span style="color:#00a79a;">Nome</span>',
        itemId: 'nome',
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
    },{
        xtype: 'actioncolumn',
        width: 75,
        dataIndex: 'checkcolumn',
        text: '<span style="color:#00a79a;">Status</span>',
        align: 'center',
        items: [{
            getClass: function (v, meta, record) {
                meta.style = 'font-size: 4px';
                if (record.get("status")) {
                    return 'fa fa-check-square';
                }
                return 'fa fa-square';
            },
            getTip: function(v, meta, record) {
                if (record.get('status')) {
                    return 'Inativar';
                }

                return 'Ativar';
            },
            handler: function() {
                console.log('Batata');
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
        bind: {
            store: '{clientes}'
        },
        listeners: { //Para esconder o botão de reload...
            afterrender: function(toolbar) {
                toolbar.down('#refresh').hide();//Buscando pelo itemId
            }
        }
    }
});