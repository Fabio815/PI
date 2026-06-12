//colocar no Grid -pasta(store) -> usuario, fazer a listagem
Ext.define( 'ProjSistemaOs.view.usuario.UsuarioGrid', {
    extend: 'Ext.grid.Panel',
    xtype: 'usuario-grid',
    requires: [
        'ProjSistemaOs.store.Usuario',
        'Ext.grid.column.Check',
        'Ext.grid.plugin.CellEditing',
        'Ext.grid.filters.Filters',
        'Ext.toolbar.Paging',
        'ProjSistemaOs.view.usuario.ConfigurarUsuarioWindow',
        'ProjSistemaOs.view.usuario.CadastroUsuarioWindow',
        'ProjSistemaOs.util.MensagemUtil'
    ],
    controller: {
        adicionarUsuario: function(){
            var me = this, vw = me.getViewModel(),
                win = Ext.create('ProjSistemaOs.view.usuario.CadastroUsuarioWindow', {
                    floating: true,
                    modal: true,
                    iconCls: 'fa fa-user-plus',
                });
            win.on('usuariosalvo', () => {
                if (vw && !vw.destroyed && !vw.isDestroying) {
                    me.getView().getStore().reload();
                }
            });
            win.show();
        },
        configurarUsuario: function() {

        },
        recarregarGridUsuarios: function() {
            var me = this, vw = me.getViewModel();
            if (vw && !vw.destroyed && !vw.isDestroying) {
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
        editarUsuario: function(edit, context) {
            var me = this, vw = me.getViewModel(),
                oldValue = edit.context.originalValue,
                newValue = edit.context.value;
            if (oldValue !== newValue) {
                Ext.Ajax.request({
                    url: 'http://localhost:8080/usuarios/atualizar',
                    method: 'PUT',
                    jsonData: context.record.getData(),
                    success: function (response) {
                        var r = Ext.JSON.decode(response.responseText, true);
                        if (r && r.sucesso) {
                            context.record.commit();
                            Avisos.mensagemSucesso(r.mensagem);
                        } else {
                            context.record.reject();
                            Avisos.contateAdm();
                        }
                    },
                    failure: function (response) {
                        context.record.reject();
                        Avisos.mostrarServidorIndisponivel();
                    }
                })
            }
        },
        listen: {
            component: {
                'usuario-grid actioncolumn#status': {
                    trocarStatus: function (a, b, e, f, h, record, k) {
                        var me = this, vw = me.getView();
                        Ext.Ajax.request({
                            url: 'http://localhost:8080/usuarios/status/atualizar',
                            method: 'POST',
                            jsonData: record.data,
                            callback: function (success, response, options) {
                                var r = Ext.JSON.decode(options.responseText);
                                if (r.sucesso) {
                                    a.getStore().reload();
                                } else {
                                    Avisos.contateAdm();
                                }
                            }
                        })
                    }
                }
            }
        }
    },
    title: 'Usuários',
    layout: 'fit',
    enableColumnHide: false,

    store: {
        type: 'usuario-listagem-store'
    },

    tbar: [{
        xtype: 'button',
        tooltip: 'Adicionar',
        iconCls: 'fa fa-plus',
        handler: 'adicionarUsuario'
    },'-', {
        xtype: 'button',
        tooltip: 'Mudar senha',
        iconCls: 'fa fa-user-cog',
        handler: 'configurarUsuario'
    }, '-', {
        xtype: 'button',
        tooltip: 'Recarregar',
        iconCls: 'fa fa-sync',
        handler: 'recarregarGridUsuarios'
    }, '->', {
        xtype: 'button',
        iconCls: 'fas fa-ban',
        tooltip: 'Limpar Pesquisa',
        listeners: {
            click: 'limparPesquisa'
        }
    }],

    columns: [{
        text: 'Id',
        itemId: 'id',
        dataIndex: 'id',
        flex: 1,
        filter: {
            type: 'number',
            menuItems: ['eq']
        }
    }, {
        text: 'Chave',
        itemId: 'chave',
        dataIndex: 'chave',
        flex: 3,
        sortable: false
    }, {
        text: 'Nome Completo',
        itemId: 'nome',
        dataIndex: 'nome',
        flex: 6,
        filter: 'string',
        editor: {
            type: 'textfield',
            allowBlank: false,
            blankText: 'Este campo é obrigatório',
        },
        renderer: function (v, meta, record){
            if (record.get('nome')) {
                return '<i class="fa fa-user"></i> ' + record.get('nome');
            }
            return '';
        }
    }, {
        text: 'Email',
        itemId: 'email',
        dataIndex: 'email',
        flex: 8,
        filter: 'string',
        editor: {
            type: 'textfield',
            allowBlank: false,
            blankText: 'Este campo é obrigatório',
        },
        renderer: function (v, meta, record){
            if (record.get('email')) {
                return '<i class="fa fa-envelope"></i> ' + record.get('email');
            }
            return '';
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
    }],
    plugins: {
        gridfilters: true,
        cellediting: {
            clicksToEdit: 2,
            listeners: {
                edit: 'editarUsuario'
            }
        }
    },
});