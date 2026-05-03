Ext.define('ProjSistemaOs.view.login.LoginPanel', {
    extend: 'Ext.form.Panel',
    alias: 'widget.loginPanel',

    requires: [
        'ProjSistemaOs.view.main.Main'
    ],

    controller: {
        enviar: function() {
            var viewport = this.getView();
            viewport.removeAll();

            //Basicamente aqui estou criando um Viewport que é um container especial que se ajusta automaticamente
            // ao tamanho da tela e colocando o main dentro dele com o layout fit que faz ocupar a tela inteiro
            Ext.create('Ext.container.Viewport', {
                layout: 'fit',
                items: [{
                    xtype: 'app-main'
                }]
            });
        }
    },

    layout: 'center',

    items: [{
        xtype: 'form',
        width: 350,
        height: 210,
        title: 'Login',
        frame: true,
        bodyPadding: '20 5 5 5',
        defaultType: 'textfield',

        items: [{
            allowBlank: false,
            fieldLabel: 'Login',
            name: 'login',
            emptyText: 'Nome do usuário',
            msgTarget: 'under',
            labelAlign: 'right',
            blankText: 'Este campo é obrigatório',
            //labelWidth: 50,
        }, {
            allowBlank: false,
            fieldLabel: 'Senha',
            name: 'senha',
            emptyText: 'Senha',
            inputType: 'password',
            labelAlign: 'right',
            blankText: 'Este campo é obrigatório',
            //labelWidth: 50,
        }],
        defaults: {
            anchor: '100%',
            labelWidth: 50
        },
        buttons: [{
            text: 'Enviar',
            handler: 'enviar'
        }]
    }]
});