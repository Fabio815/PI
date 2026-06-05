Ext.define('ProjSistemaOs.view.usuario.CadastroUsuarioWindow', {
    extend: 'Ext.panel.Panel',
    //alias: 'widget.cadastro-usuario',
    xtype: 'cadastrar-usuario',
    controller:{
        cadastrar: function (){

		var me = this, vw = me.getView(),
		form = vw.down('form').getForm().getValues();
        console.log(form);

		Ext.Ajax.request({
			url: 'http://localhost:8080/usuarios/cadastro',
			method: 'POST',
			jsonData: form,
			success: function (conn, response, options, eOpts) {
				let r = Ext.JSON.decode(conn.responseText, true);
				console.log(r);
				if (r && r.resposta.sucesso) {
					vw.fireEvent('clientesalvo'); //Dispara o evento quando salva o usuario.
					Avisos.mensagemSucesso(r.resposta.mensagem);
					vw.close();
				} else if (r) {
					Avisos.mensagemAviso(r.resposta.mensagem);
				} else {
					Avisos.mostrarServidorIndisponivel();
				}
			},
			failure: function (conn, response, options, eOpts) {
				Avisos.mostrarServidorIndisponivel();
			}
		});
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
                    emptyText: '(00) 00000-0000',
                    maxLength: 15
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
                xtype: 'textfield',
                fieldLabel: 'Senha',
                name: 'senha',
                labelWidth: 50,
                labelAlign: 'right',
                inputType: 'password',
                allowBlank: false,
                emptyText: 'Digite a senha'
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
        disabled: true,
        formBind: true
    }]
});