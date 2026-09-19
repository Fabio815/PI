Ext.define('ProjSistemaOs.view.login.LoginPanel', {
    extend: 'Ext.form.Panel',
    alias: 'widget.loginPanel',

    requires: [
        'ProjSistemaOs.view.main.Main',
        'ProjSistemaOs.util.MensagemUtil',
        'ProjSistemaOs.util.Config',
        'ProjSistemaOs.util.SessaoUtil',
        'ProjSistemaOs.view.usuario.RedefinirSenhaWindow'
    ],

    controller: {
        enviar: function() {
            var viewport = this.getView(),
                me = this, vw = me.getView(),
                form = vw.down('form').getForm().getValues();
            Ext.Ajax.request({
                url: sistemaOsLocal.apiUrl + '/auth/login',
                method: 'POST',
                jsonData: {
                    email: form.login,
                    senha: form.senha
                },
                scope: this,
                success: function(conn, response, options, eOpts) {
                    let r = Ext.JSON.decode(conn.responseText, true);
                    if (r && r.id) {
                        SessaoUtil.definirUsuarioLogado(r);
                        viewport.removeAll();
                        Ext.create('Ext.container.Viewport', {
                            layout: 'fit',
                            items: [{
                                xtype: 'app-main',
                                perfil: r.chave
                            }]
                        });
                    } else {
                        Avisos.mensagemAviso('Login inválido. Verifique email e senha.');
                    }
                },
                failure: function(conn, response, options, eOpts) {
                    var r = Ext.JSON.decode(conn.responseText, true);
                    if (r && r.mensagemErro) {
                        Avisos.mensagemAviso(r.mensagemErro);
                    } else {
                        Avisos.mostrarServidorIndisponivel();
                    }
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
        height: 250,
        title: 'Login',
        iconCls: 'fa fa-key',
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
            itemId: 'senhaId',
            name: 'senha',
            emptyText: 'Senha',
            inputType: 'password',
            labelAlign: 'right',
            blankText: 'Este campo é obrigatório',
            //labelWidth: 50,
        }, {
            xtype: 'checkbox',
            boxLabel: 'Mostrar senha',
            listeners: {
                change: function(cb, checked) {
                    var campoSenha = cb.up('form').down('#senhaId');
                    campoSenha.inputEl.dom.type = checked ? 'text' : 'password';
                }
            }
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