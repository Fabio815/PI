Ext.define('ProjSistemaOs.view.cliente.ClienteGrid', {
    extend: 'Ext.grid.Panel',
    xtype: 'clienteGrid',

    requires: [
        'Ext.grid.column.Action',
        'Ext.grid.column.Check',
        'Ext.grid.plugin.CellEditing',
        'Ext.grid.filters.Filters',
        'Ext.toolbar.Paging',

        'ProjSistemaOs.store.Cliente',
        'ProjSistemaOs.view.cliente.ClienteWindow',
        'ProjSistemaOs.util.MensagemUtil',
        'ProjSistemaOs.util.ClienteUtil',
        'ProjSistemaOs.util.Config'
    ],

    controller: {
        adicionarCliente: function(){
            console.log(this);
            var me = this, vw = me.getViewModel();
            /*var me = this, vw = me.getViewModel(),
                win = Ext.create('ProjSistemaOs.view.cliente.ClienteWindow');

            win.on('clienteSalvo', () => { //Aqui está escutando o envento que é disparadp quando salva o cliente.
                if (vw && !vw.destroyed && !vw.isDestroying) {
                    me.getView().getStore().reload();
                }
            });
            win.show();*/
            Ext.create('ProjSistemaOs.view.cliente.ClienteWindow', {
                floating: true,
                modal: true,
                iconCls: 'fa fa-plus',
                listeners: {
                    'clienteSalvo': function(){
                        if (vw && !vw.destroyed && !vw.isDestroying) {
                            me.getView().getStore().reload();
                        }
                    }
                }
            }).show();
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
                    url: sistemaOsLocal.apiUrl + '/cliente/atualizar/' + record.get('id'),
                    method: 'PUT',
                    jsonData: dadosFormato,
                    success: function (response) {
                        var r = Ext.decode(response.responseText, true);
                        if (r && r.resposta.sucesso) {
                            record.commit();
                            //Avisos.mensagemSucesso(r.resposta.mensagem);
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
        limparPesquisa: function (e, t, eOpts) {
            let a = e.up('grid');
            if (a) {
                a.filters.clearFilters();
                a.getStore().getSorters().removeAll();
            }
        },
        listen: {
            component: {
                'clienteGrid actioncolumn#status': {
                    trocarStatus: function (a, b, e, f, h, record, k) {
                        let me = this, vw = me.getView();
                        Ext.Ajax.request({
                            url: sistemaOsLocal.apiUrl + '/cliente/status',
                            method: 'POST',
                            jsonData: record.data,
                            callback: function (success, response, options){
                                if (vw && !vw.destroyed && !vw.isDestroying) {
                                    let r = Ext.decode(options.responseText, true);
                                    if (r) {
                                        if (r && r.sucesso) {
                                            a.getStore().reload();
                                        } else {
                                            Avisos.mensagemAviso(r.mensagem);
                                        }
                                    } else {
                                        Avisos.mostrarServidorIndisponivel();
                                    }
                                }
                            }
                        });
                    }
                }
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
		handler: 'adicionarCliente'
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
    columns: [{
        text: "Id",
        itemId: 'id',
        dataIndex: 'id',
        width: 60,
        filter: {
            type: 'number',
            menuItems: ['eq']
        }
    }, {
        text: "Nome",
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