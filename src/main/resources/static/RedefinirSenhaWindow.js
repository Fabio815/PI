Ext.define('ProjSistemaOs.view.usuario.RedefinirSenhaWindow', {
    extend: 'Ext.window.Window',
    alias: 'widget.redefinir-senha',

    title: 'Redefinir Senha',
    width: 450,
    modal: true,
    resizable: false,
    layout: 'fit',

    items: [{
        xtype: 'form',
        bodyPadding: 20,

        defaults: {
            xtype: 'textfield',
            anchor: '100%',
            allowBlank: false,
            labelWidth: 150,
            inputType: 'password'
        },

        items: [{
            fieldLabel: 'Nova Senha',
            name: 'novaSenha',
            emptyText: 'Digite a nova senha'
        }, {
            fieldLabel: 'Confirmar Senha',
            name: 'confirmarSenha',
            emptyText: 'Digite novamente a senha'
        }]
    }],

    buttons: [{
        text: 'Salvar',
        iconCls: 'x-fa fa-save',

        handler: function(btn) {

            var win = btn.up('window');
            var form = win.down('form');
            var valores = form.getValues();

            if (valores.novaSenha !== valores.confirmarSenha) {
                Ext.Msg.alert(
                    'Erro',
                    'As senhas não coincidem.'
                );
                return;
            }

            Ext.Msg.alert(
                'Sucesso',
                'Senha redefinida com sucesso!'
            );
        }
    }, {
        text: 'Cancelar',

        handler: function(btn) {
            btn.up('window').close();
        }
    }]
});