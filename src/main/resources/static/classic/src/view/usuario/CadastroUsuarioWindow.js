Ext.define('ProjSistemaOs.view.usuario.CadastroUsuarioWindow', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.cadastrar-usuario',
    //xtype: 'cadastrar-usuario',

    controller:{
        cadastrar: function (){
            var me = this, vw = me.getView(),
            form = vw.down('form').getForm().getValues();

            Ext.Ajax.request({
                url: '/usuarios/cadastro',
                method: 'POST',
                jsonData: form,
                success: function (conn, response, options, eOpts) {
                    let r = Ext.JSON.decode(conn.responseText, true);
                    if (r && r.resposta.sucesso) {
                        vw.fireEvent('usuariosalvo');
                        Avisos.mensagemSucesso(r.resposta.mensagem);
                        vw.close();
                    } else {
                        Avisos.mensagemAviso(r.resposta.mensagem);
                    }
                },
                failure: function (conn, response, options, eOpts) {
                    let r = Ext.JSON.decode(conn.responseText, true);
                    Avisos.mensagemAviso(r.mensagem);
                }
            });
        },
        onValidacaoUsuario: function (form, valid) {
            this.lookup('btnSalvar').setDisabled(!valid);
        }
    },

    title: 'Cadastro Usuário',
    layout: 'fit',
    resizable: false,
    width: 650,
    bodyPadding: 15,
    ui: 'light',
    padding: 5,
    shadow: true,
    style: {
        backgroundColor: "#ececec",
        borderRadius: '5px'
    },
    header: {
        style: {
            backgroundColor: "#ececec"
        }
    },
    items: [{
        xtype: 'form',
        layout: 'anchor',
        defaults: {
            anchor: '100%',
            labelAlign: 'right',
            margin: '5 0 0 0'
        },
        listeners: {
            validitychange: 'onValidacaoUsuario'
        },
        items: [{
            xtype: 'container',
            layout: 'anchor',
            defaults: {
                anchor: '100%'
            },
            items: [{
                xtype: 'fieldcontainer',
                layout: 'hbox',
                defaultType: 'textfield',
                defaults: {
                    labelAlign: 'right',
                    allowBlank: false,
                    blankText: 'O campo é obrigatório'
                },

                items: [{
                    fieldLabel: 'Nome',
                    name: 'nome',
                    labelWidth: 50,
                    flex: 1,
                    margin: '0 10 0 0',
                    emptyText: 'Digite o nome'
                }, {
                    fieldLabel: 'Sobrenome',
                    name: 'sobrenome',
                    labelWidth: 70,
                    flex: 1,
                    emptyText: 'Digite o sobrenome'
                }]
            }, {
                xtype: 'textfield',
                fieldLabel: 'Email',
                name: 'email',
                vtype: 'email',
                labelWidth: 50,
                allowBlank: false,
                labelAlign: 'right',
                emptyText: 'Digite o email'
            }, {
                xtype: 'fieldcontainer',
                layout: 'hbox',
                defaults: {
                    labelAlign: 'right',
                    allowBlank: false,
                    blankText: 'O campo é obrigatório'
                },

                items: [{
                    xtype: 'textfield',
                    fieldLabel: 'Telefone',
                    name: 'telefone',
                    labelWidth: 50,
                    flex: 1,
                    margin: '0 10 0 0',
                    maskRe: /[0-9]/,
                    emptyText: '(00) 00000-0000',
                    maxLength: 15,
                    listeners: {
                        change: function(field, value) {
                            value = value.replace(/\D/g, '');

                            if (value.length > 11) {
                                value = value.substring(0, 11);
                            }

                            if (value.length > 10) {
                                value = value.replace(
                                    /^(\d{2})(\d{5})(\d{4}).*/,
                                    '($1) $2-$3'
                                );
                            } else {
                                value = value.replace(
                                    /^(\d{2})(\d{4})(\d{0,4}).*/,
                                    '($1) $2-$3'
                                );
                            }

                            field.suspendEvents();
                            field.setValue(value);
                            field.resumeEvents();
                        }
                    }
                }, {
                    xtype: 'combobox',
                    fieldLabel: 'Perfil',
                    name: 'chave',
                    labelWidth: 50,
                    flex: 1,
                    editable: false,
                    queryMode: 'local',
                    forceSelection: true,
                    store: [
                        'administrador',
                        'funcionário'
                    ]
                }]
            }, {
                xtype: 'fieldcontainer',
                items: [{
                    xtype: 'textfield',
                    fieldLabel: 'Senha',
                    name: 'senha',
                    width: '300px',
                    labelWidth: 50,
                    labelAlign: 'right',
                    inputType: 'password',
                    allowBlank: false,
                    emptyText: 'Digite a senha'
                }]
            }]
        }]
    }],
    buttons: [{
        text: 'Cancelar',
        iconCls: 'fa fa-times',
        handler: function(btn) {
            btn.up('cadastrar-usuario').destroy();
        }
    }, {
        text: 'Cadastrar',
        iconCls: 'fa fa-check',
        handler: 'cadastrar',
        reference: 'btnSalvar',
        disabled: true,
    }]
});