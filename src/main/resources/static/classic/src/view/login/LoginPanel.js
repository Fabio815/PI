Ext.define('ProjSistemaOs.view.login.LoginPanel', {
    extend: 'Ext.form.Panel',
    alias: 'widget.loginPanel',

    requires: [
        'ProjSistemaOs.view.main.Main',
        'ProjSistemaOs.util.MensagemUtil',
        'ProjSistemaOs.util.Config',
        'ProjSistemaOs.view.usuario.RedefinirSenhaWindow'
    ],

    controller: {
        enviar: function() {
            var viewport = this.getView(),
                me = this, vw = me.getView(),
                form = vw.down('form').getForm().getValues();
            Ext.Ajax.request({
                url: sistemaOsLocal.apiUrl + '/usuarios/login',
                method: 'POST',
                jsonData: {
                    email: form.login,
                    senha: form.senha
                },
                scope: this,
                success: function(conn, response, options, eOpts) {
                    let r = Ext.JSON.decode(conn.responseText, true);
                    if (r && r.resposta.sucesso) {
                        viewport.removeAll();
                        //Basicamente aqui estou criando um Viewport que é um container especial que se ajusta automaticamente
                        // ao tamanho da tela e colocando o main dentro dele com o layout fit que faz ocupar a tela inteiro
                        Ext.create('Ext.container.Viewport', {
                            layout: 'fit',
                            items: [{
                                xtype: 'app-main',
                                perfil: r.chave
                            }]
                        });
                        //localStorage.setItem('perfil', r.chave);
                    } else if (r && !r.resposta.sucesso) {
                        Avisos.mensagemAviso(r.resposta.mensagem);
                    } else {
                        Avisos.contateAdm();
                    }
                },
                failure: function(response) {
                    Avisos.mostrarServidorIndisponivel();
                }
            })
        },
        recuperarSenha: function() {
            var me = this, vw = me.getView();
            form = vw.down('form').getForm().getValues();
            Ext.Ajax.request({
                url: sistemaOsLocal.apiUrl + '/auth/recuperar',
                method: 'POST',
                jsonData: {
                    email: form.login,
                },
                scope: this,
                success: function (conn, response, options, eOpts) {
                    Avisos.mensagemSucesso(conn.responseText);
                },
                failure: function(response) {
                    Avisos.mostrarServidorIndisponivel();
                }
            })
        },
        onValidacaoUsuario: function (form, valid) {
            this.lookup('enviarSenha').setDisabled(!valid);
            this.lookup('recuperar').setDisabled(!valid);
        }
    },

    layout: 'center',
    listeners: {
        afterrender: function(panel) {

            var hash = window.location.hash;

            if (hash.indexOf('redefinir-senha') !== -1) {

                var token = hash.split('?token=')[1];

                Ext.create(
                    'ProjSistemaOs.view.usuario.RedefinirSenhaWindow',
                    {
                        token: token
                    }
                ).show();
            }
        }
    },

    items: [{
        xtype: 'form',
        width: 350,
        height: 210,
        title: 'Login',
        frame: true,
        bodyPadding: '20 5 5 5',
        defaultType: 'textfield',
        listeners: {
            validitychange: 'onValidacaoUsuario',
        },
        items: [{
            allowBlank: false,
            itemId: 'login',
            fieldLabel: 'Login',
            name: 'login',
            emptyText: 'Nome do usuário',
            msgTarget: 'under',
            labelAlign: 'right',
            blankText: 'Este campo é obrigatório',
            //labelWidth: 50,
        }, {
            //allowBlank: false,
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
            text: 'Recuperar senha',
            handler: 'recuperarSenha',
            reference: 'recuperar',
            disabled: true
        },'->',{
            text: 'Enviar',
            handler: 'enviar',
            reference: 'enviarSenha',
            disabled: true,
        }]
    }]
});