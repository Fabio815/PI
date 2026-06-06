//colocar no Grid -pasta(store) -> usuario, fazer a listagem
Ext.define( 'ProjSistemaOs.view.usuario.UsuarioGrid', {
    extend: 'Ext.grid.Panel',
    xtype: 'usuario-grid',
    requires: [
        'ProjSistemaOs.store.Usuario',
        'Ext.grid.column.Check',
        'Ext.grid.plugin.CellEditing',
        'Ext.grid.filters.Filters',
        'Ext.toolbar.Paging'

    ],
    controller:{
        adicionarUsuario: function (){
            Ext.create('ProjSistemaOs.view.usuario.CadastroUsuarioWindow', {
                floating: true,
                modal: true,
                iconCls: 'fa fa-user-plus',
            }).show();
        },
        configurarUsuario: function (){
            var me = this, vw = me.getViewModel(),
                win = Ext.create('ProjSistemaOs.view.usuario.ConfigurarUsuarioWindow');
            win.show();
        },
        redefinirSenha: function () {
            Ext.Msg.confirm(
                'Confirmação',
                'Deseja redefinir a senha deste usuário?',
                function (btn) {
                    if (btn === 'yes') {
                        Ext.Msg.alert(
                            'Sucesso',
                            'A senha foi redefinida.'
                        );
                    }
                }
            );
        },

        salvarUsuario: function () {
            Ext.Msg.alert('Salvar', 'Usuário atualizado.');
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
        tooltip: 'Configurar',
        iconCls: 'fa fa-user-cog',
        handler: 'configurarUsuario'
    }, '-', {
        xtype: 'button',
        tooltip: 'Recarregar',
        iconCls: 'fa fa-sync',
        handler: 'recarregarGrid'
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
    },{
       xtype: 'checkcolumn',
       text: 'Ativo',
       dataIndex: 'status',
       width: 80,
       filter: {
           type: 'boolean',
           yesText: 'Sim',
           noText: 'Não',
           default: true
       }
    }],
    plugins: {
        gridfilters: true,
        cellediting: {
            clicksToEdit: 2
        }
    },
});