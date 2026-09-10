Ext.define('ProjSistemaOs.view.os.CadastroOsGrid', {
    extend: 'Ext.grid.Panel',
    xtype: 'cadastro-os-grid',

    requires: [
        'ProjSistemaOs.view.os.CadastroOsWindow',
        'ProjSistemaOs.view.os.AtualizarOsWindow',
        'ProjSistemaOs.view.os.InformacoesOsWindow',
        'ProjSistemaOs.store.Os',
        'Ext.grid.column.Action',
        'Ext.grid.column.Check',
        'Ext.grid.plugin.CellEditing',
        'Ext.grid.filters.Filters',
        'Ext.toolbar.Paging',
    ],

    plugins: ['gridfilters'],

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
        },
        adicionarOs: function () {
            var me = this;

            Ext.create('ProjSistemaOs.view.os.CadastroOsWindow', {
                floating: true,
                modal: true,
                iconCls: 'fa fa-thin fa-plus',
                listeners: {
                    ossalva: function () {
                        me.recarregarGrid();
                    }
                }
            }).show();
        },
        carregarInformacoesOs: function () {
            var me = this;
            var grid = me.getView();
            var record = grid.getSelection()[0];

            if (!record) {
                Ext.Msg.alert('Atenção', 'Selecione uma OS para visualizar.');
                return;
            }
            Ext.create('ProjSistemaOs.view.os.InformacoesOsWindow', {
                floating: true,
                modal: true,
                iconCls: 'fa fa-eye',
                osId: record.get('id'),
            }).show();
        },
        editarOs: function () {
            var me = this, vw = me.getViewModel();
            Ext.create('ProjSistemaOs.view.os.AtualizarOsWindow', {
                floating: true,
                modal: true,
                iconCls: 'fa fa-pen'
            }).show();
        },
        listen: {
            component: {
                'cadastro-os-grid actioncolumn#status': {
                    trocarStatus: function (a, b, e, f, h, record, k) {
                        let me = this, vw = me.getView();
                        Ext.Ajax.request({
                            url: sistemaOsLocal.apiUrl + '/os/status/' + record.get('id'),
                            method: 'PUT',
                            jsonData: record.data,
                            callback: function (success, response, options){
                                if (vw && !vw.destroyed && !vw.isDestroying) {
                                    let r = Ext.decode(options.responseText, true);
                                    if (r) {
                                        if (r) {
                                            a.getStore().reload();
                                        } else {
                                            Avisos.mensagemAviso("Contate o administrador!");
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

    store: {
        type: 'os-listagem-store'
    },

    title: 'Clientes',
    layout: 'fit',
    tbar: [{
        xtype: 'button',
        tooltip: 'Adicionar',
        iconCls: 'fa fa-plus',
        handler: 'adicionarOs'
    }, '-', {
        xtype: 'button',
        tooltip: 'Vizualizar',
        iconCls: 'fa fa-eye',
        handler: 'carregarInformacoesOs'
    }, '-', {
        xtype: 'button',
        tooltip: 'Editar',
        iconCls: 'fa fa-pen',
        handler: 'editarOs'
    },'-', {
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
        filter: {
            type: 'number',
            menuItems: ['eq']
        },
        flex: 1
    }, {
        text: 'Data de criação',
        dataIndex: 'dataEmissao',
        flex: 2,
        renderer: function (value) {
            return value ? Ext.Date.format(value, 'd/m/Y') : '';
        }
    }, {
        text: 'Nome cliente',
        dataIndex: 'nomeCliente',
        filter: 'string',
        flex: 4
    }, {
        text: 'Telefone',
        dataIndex: 'telefone',
        flex: 2
    }, {
        text: 'Preço',
        dataIndex: 'valorTotal',
        flex: 1
    }, {
        text: 'Situação',
        dataIndex: 'situacao',
        flex: 3,
        renderer: function (value, metaData) {
            let cor = '';
            switch (value) {
                case 'Concluído':
                    cor = 'background-color: #46C248; color: white;';
                    break;
                case 'Em Andamento':
                    cor = 'background-color: #3D69D1; color: white;';
                    break;
                case 'Cancelado':
                    cor = 'background-color: #C74040; color: white;';
                    break;
                default:
                    cor = '';
                    break;
            }
            metaData.style = cor;
            return value;
        }
    }, {
        text: 'Data de finalização',
        dataIndex: 'dataFim',
        flex: 2,
        renderer: function (value) {
            return value ? Ext.Date.format(value, 'd/m/Y') : '';
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
    }]
});