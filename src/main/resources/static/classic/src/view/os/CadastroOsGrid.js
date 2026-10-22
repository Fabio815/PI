Ext.define('ProjSistemaOs.view.os.CadastroOsGrid', {
    extend: 'Ext.grid.Panel',
    xtype: 'cadastro-os-grid',

    requires: [
        'ProjSistemaOs.view.os.CadastroOsWindow',
        'ProjSistemaOs.view.os.AtualizarOsWindow',
        'ProjSistemaOs.view.os.InformacoesOsWindow',
        'ProjSistemaOs.view.os.HistoricoOsWindow',
        'ProjSistemaOs.view.os.ComentariosOsWindow',
        'ProjSistemaOs.view.os.AtribuirOsWindow',
        'ProjSistemaOs.view.os.AlterarSituacaoOsWindow',
        'ProjSistemaOs.view.os.RelatorioOsWindow',
        'ProjSistemaOs.util.Sessao',
        'ProjSistemaOs.util.OsOperacional',
        'ProjSistemaOs.store.Os',
        'Ext.grid.column.Action',
        'Ext.grid.column.Check',
        'Ext.grid.plugin.CellEditing',
        'Ext.grid.filters.Filters',
        'Ext.toolbar.Paging',
    ],

    plugins: ['gridfilters'],

    controller: {
        init: function () {
            var view = this.getView(), admin = Sessao.isAdm();
            view.lookupReference('btnAtribuir').setHidden(!admin);
            view.lookupReference('btnReabrir').setHidden(!admin);
        },
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
            var me = this;
            var grid = me.getView();
            var record = grid.getSelection()[0];

            if (!record) {
                Ext.Msg.alert('Atenção', 'Selecione uma OS para editar.');
                return;
            }

            if (!Sessao.podeEditarOs(record.get('usuarioId'))) {
                Ext.Msg.alert('Atenção', 'Você só pode editar as ordens de serviço que você criou.');
                return;
            }
            if (OsOperacional.estaEncerrada(record.data)) {
                Ext.Msg.alert('Atenção', 'OS encerrada: reabra-a antes de editar seus campos.');
                return;
            }

            Ext.create('ProjSistemaOs.view.os.AtualizarOsWindow', {
                floating: true,
                modal: true,
                iconCls: 'fa fa-pen',
                osId: record.get('id'),
                listeners: {
                    ossalva: function () {
                        me.recarregarGrid();
                    }
                }
            }).show();
        },
        onSelectionChange: function (selModel, selected) {
            var me = this,
                record = selected[0], encerrada = record && OsOperacional.estaEncerrada(record.data),
                podeEditar = !!record && !encerrada && Sessao.podeEditarOs(record.get('usuarioId'));
            me.lookupReference('btnEditar').setDisabled(!podeEditar);
            me.lookupReference('btnSituacao').setDisabled(!podeEditar);
            me.lookupReference('btnAtribuir').setDisabled(!record || !Sessao.isAdm());
            me.lookupReference('btnReabrir').setDisabled(!record || !encerrada || !Sessao.isAdm());
            me.lookupReference('btnComentarios').setDisabled(!record);
            me.lookupReference('btnHistorico').setDisabled(!record);
        },
        abrirHistorico: function () {
            var me = this;
            var grid = me.getView();
            var record = grid.getSelection()[0];

            if (!record) {
                Ext.Msg.alert('Atenção', 'Selecione uma OS para ver o histórico.');
                return;
            }

            Ext.create('ProjSistemaOs.view.os.HistoricoOsWindow', {
                floating: true,
                modal: true,
                osId: record.get('id')
            }).show();
        },
        abrirComentarios: function () {
            var grid = this.getView(), record = grid.getSelection()[0];
            if (!record) {
                Ext.Msg.alert('Atenção', 'Selecione uma OS para ver os comentários.');
                return;
            }
            Ext.create('ProjSistemaOs.view.os.ComentariosOsWindow', {
                osId: record.get('id'),
                modal: true,
                listeners: { comentariosalterados: this.recarregarGrid, scope: this }
            }).show();
        },
        abrirAtribuicao: function () {
            var me = this, record = me.getView().getSelection()[0];
            if (!record) {
                Ext.Msg.alert('Atenção', 'Selecione uma OS para atribuir responsável.');
                return;
            }
            if (!Sessao.isAdm()) {
                Ext.Msg.alert('Atenção', 'Somente administradores podem atribuir ou reatribuir uma OS.');
                return;
            }
            Ext.create('ProjSistemaOs.view.os.AtribuirOsWindow', {
                osId: record.get('id'),
                responsavelIdAtual: record.get('responsavelId'),
                responsavelNomeAtual: record.get('responsavelNome'),
                listeners: { ossalva: this.recarregarGrid, scope: this }
            }).show();
        },
        abrirSituacao: function () {
            var me = this, record = me.getView().getSelection()[0];
            if (!record) {
                Ext.Msg.alert('Atenção', 'Selecione uma OS para alterar a situação.');
                return;
            }
            if (!Sessao.podeEditarOs(record.get('usuarioId'))) {
                Ext.Msg.alert('Atenção', 'Você só pode alterar a situação das ordens de serviço que criou.');
                return;
            }
            if (OsOperacional.estaEncerrada(record.data)) {
                Ext.Msg.alert('Atenção', 'OS encerrada: reabra-a antes de alterar a situação.');
                return;
            }
            Ext.create('ProjSistemaOs.view.os.AlterarSituacaoOsWindow', {
                osId: record.get('id'),
                situacaoAtual: record.get('situacao'),
                listeners: { ossalva: this.recarregarGrid, scope: this }
            }).show();
        },
        reabrirOs: function () {
            var me = this, record = me.getView().getSelection()[0];
            if (!record) {
                Ext.Msg.alert('Atenção', 'Selecione uma OS para reabrir.');
                return;
            }
            if (!Sessao.isAdm()) {
                Ext.Msg.alert('Atenção', 'Somente administradores podem reabrir OS.');
                return;
            }
            if (!OsOperacional.estaEncerrada(record.data)) {
                Ext.Msg.alert('Atenção', 'Somente uma OS concluída ou cancelada pode ser reaberta.');
                return;
            }
            Ext.Msg.confirm('Reabrir OS', 'Reabrir a OS #' + Ext.String.htmlEncode(String(record.get('id'))) + ' para Pendente?', function (choice) {
                if (choice !== 'yes') { return; }
                Ext.Ajax.request({
                    url: OsOperacional.apiUrl('/os/' + encodeURIComponent(record.get('id')) + '/reabrir'),
                    method: 'POST',
                    callback: function (options, success, response) {
                        if (success) {
                            me.recarregarGrid();
                        } else {
                            OsOperacional.exibirErro(response, 'Não foi possível reabrir a OS.');
                        }
                    }
                });
            });
        },
        abrirRelatorio: function () {
            Ext.create('ProjSistemaOs.view.os.RelatorioOsWindow', { floating: true, modal: true }).show();
        },
        listen: {
            component: {
                'cadastro-os-grid actioncolumn#status': {
                    trocarStatus: function (a, b, e, f, h, record, k) {
                        let me = this, vw = me.getView();

                        if (OsOperacional.estaEncerrada(record.data)) {
                            record.set('_status', record.get('status'));
                            Ext.Msg.alert('Atenção', 'OS encerrada: reabra-a antes de alterar o status ativo/inativo.');
                            return;
                        }
                        if (!Sessao.podeEditarOs(record.get('usuarioId'))) {
                            record.set('_status', record.get('status'));
                            Ext.Msg.alert('Atenção', 'Você só pode alterar as ordens de serviço que você criou.');
                            return;
                        }

                        Ext.Ajax.request({
                            url: sistemaOsLocal.apiUrl + '/os/status/' + record.get('id'),
                            method: 'PUT',
                            jsonData: record.data,
                            callback: function (options, success, response) {
                                if (vw && !vw.destroyed && !vw.isDestroying) {
                                    if (success) {
                                        a.getStore().reload();
                                    } else {
                                        record.set('_status', record.get('status'));
                                        OsOperacional.exibirErro(response, 'Não foi possível alterar o status da OS.');
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

    selModel: {
        selType: 'rowmodel',
        listeners: {
            selectionchange: 'onSelectionChange'
        }
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
        reference: 'btnEditar',
        disabled: true,
        handler: 'editarOs'
    },'-', {
        xtype: 'button',
        tooltip: 'Recarregar',
        iconCls: 'fa fa-sync',
        handler: 'recarregarGrid',
    }, '-', {
        xtype: 'button',
        tooltip: 'Histórico',
        iconCls: 'fa fa-scroll',
        reference: 'btnHistorico',
        disabled: true,
        handler: 'abrirHistorico'
    }, '-', {
        xtype: 'button',
        tooltip: 'Comentários',
        iconCls: 'fa fa-comment',
        reference: 'btnComentarios',
        disabled: true,
        handler: 'abrirComentarios'
    }, '-', {
        xtype: 'button',
        tooltip: 'Alterar situação',
        iconCls: 'fa fa-exchange-alt',
        reference: 'btnSituacao',
        disabled: true,
        handler: 'abrirSituacao'
    }, '-', {
        xtype: 'button',
        tooltip: 'Atribuir/reatribuir responsável (ADM)',
        iconCls: 'fa fa-user-tag',
        reference: 'btnAtribuir',
        disabled: true,
        hidden: true,
        handler: 'abrirAtribuicao'
    }, '-', {
        xtype: 'button',
        tooltip: 'Reabrir OS (ADM)',
        iconCls: 'fa fa-undo',
        reference: 'btnReabrir',
        disabled: true,
        hidden: true,
        handler: 'reabrirOs'
    }, '-', {
        xtype: 'button',
        text: 'Relatório',
        iconCls: 'fa fa-chart-bar',
        handler: 'abrirRelatorio'
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
        text: 'Criado por',
        dataIndex: 'usuarioNome',
        filter: 'string',
        flex: 2
    }, {
        text: 'Responsável',
        dataIndex: 'responsavelNome',
        filter: 'string',
        flex: 2,
        renderer: function (value) { return Ext.String.htmlEncode(value || ''); }
    }, {
        text: 'Preço',
        dataIndex: 'valorTotal',
        align: 'right',
        renderer: function(value, metaData) {
            metaData.style = 'text-align:right;font-variant-numeric:tabular-nums;';
            if (Ext.isNumber(value)) {
                return new Intl.NumberFormat('pt-BR', {
                    style: 'currency',
                    currency: 'BRL'
                }).format(value);
            }
            return value;
        },
        flex: 2
    }, {
        text: 'Situação',
        dataIndex: 'situacao',
        flex: 3,
        renderer: function (value, metaData) {
            let cor = '';
            switch (OsOperacional.normalizarSituacao(value)) {
                case 'CONCLUIDO':
                    cor = 'background-color: #46C248; color: white;';
                    break;
                case 'EM_ANDAMENTO':
                    cor = 'background-color: #3D69D1; color: white;';
                    break;
                case 'CANCELADO':
                    cor = 'background-color: #C74040; color: white;';
                    break;
                default:
                    cor = '';
                    break;
            }
            metaData.style = cor;
            return Ext.String.htmlEncode(value || '');
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
                if (OsOperacional.estaEncerrada(record.data)) {
                    return 'x-item-disabled far fa-lock';
                }
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
                if (OsOperacional.estaEncerrada(record.data)) {
                    return 'OS encerrada. Reabra antes de alterar o status ativo/inativo.';
                }
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
