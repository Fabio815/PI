Ext.define('ProjSistemaOs.view.usuario.ConfigurarUsuarioWindow', {
    extend: 'Ext.window.Window',
    alias: 'widget.configurar-usuario',

    title: 'Configurar Usuário',
    layout: 'fit',
    resizable: false,
    modal: true,
    width: 650,
    height: 500,
    bodyPadding: 10,

    items: [{
        xtype: 'form',
        bodyPadding: 10,
        defaultType: 'textfield',
        defaults: {
            anchor: '100%'
        },

        items: [{
            xtype: 'fieldset',
            title: 'Informações do Usuário',
            defaultType: 'textfield',

            items: [{
                fieldLabel: 'Nome',
                emptyText: 'nome',
                name: 'nome'
            }, {
                fieldLabel: 'Sobrenome',
                emptyText: 'sobrenome',
                name: 'sobrenome'
            }, {
                fieldLabel: 'Email',
                emptyText: 'email',
                name: 'email'
            }, {
                fieldLabel: 'Telefone',
                emptyText: 'telefone',
                name: 'telefone'
            },{
                xtype: 'combobox',
                fieldLabel: 'Status',
                name: 'status',
                allowBlank: false,
                store: ['Ativo', 'Inativo']
            }]
        }],

        buttons: [{
            text: 'Cadastrar',
            disabled: true,
            formBind: true
        }, {
            text: 'Redefinir Senha',
            iconCls: 'fa fa-key',
            handler: 'redefinirSenha'
        }, {
            text: 'Salvar',
            iconCls: 'fa fa-save',
            handler: 'salvarUsuario'
        }, {
            text: 'Excluir',
            iconCls: 'fa fa-trash',
            handler: 'excluirUsuario'
        }]
    }]
});