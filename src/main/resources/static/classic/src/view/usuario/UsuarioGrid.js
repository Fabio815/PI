//criação do usuario e edição - formulario de cadastro
Ext.define( 'ProjSistemaOs.view.usuario.UsuarioGrid', {
    extend: 'Ext.grid.Panel',
    xtype: 'usuario-grid',
    requìred: [
          'ProjSistemaOs.store.Usuario'
    ],
    controller:{
        adicionarUsuario: function (){
            var me = this, vw = me.getViewModel(),
                win = Ext.create('ProjSistemaOs.view.usuario.CadastroUsuarioWindow');
            win.show();//Mostrar a janela
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
        },

        excluirUsuario: function () {
            Ext.Msg.confirm(
                'Confirmação',
                'Deseja excluir este usuário?',
                function (btn) {
                    if (btn === 'yes') {
                        Ext.Msg.alert(
                            'Excluído',
                            'Usuário removido.'
                        );
                    }
                }
            );
        }
    },
    title: 'Usuários',
    layout: 'fit',

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
    }, '-',{
        xtype: 'button',
        tooltip: 'Recarregar',
        iconCls: 'fa fa-sync',
        handler: 'recarregarGrid'
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
        filter: 'string'
    }, {
        text: 'Nome Completo',
        itemId: 'nome',
        dataIndex: 'nome',
        flex: 6,
        filter: 'string'
    }, {
        text: 'Email',
        itemId: 'email',
        dataIndex: 'email',
        flex: 8
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
    }]

});