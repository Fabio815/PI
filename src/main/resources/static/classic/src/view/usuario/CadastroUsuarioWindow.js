Ext.define('ProjSistemaOs.view.usuario.CadastroUsuarioWindow', {
    extend: 'Ext.window.Window',
    alias: 'widget.cadastro-usuario',

    title: 'Cadastro Usuário',
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
                fieldLabel: 'Perfil',
                name: 'perfil',
                allowBlank: false,
                store: ['Administrador', 'Funcionário']
            }, {
                fieldLabel: 'Senha',
                emptyText: 'senha',
                name: 'senha'
            }]
        }],

        buttons: [{
            text: 'Cadastrar',
            disabled: true,
            formBind: true
        }]
    }]
});