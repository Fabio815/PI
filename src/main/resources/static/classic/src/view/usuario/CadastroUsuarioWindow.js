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
        bodyPadding: 10,
        defaultType: 'textfield',
        defaults: {
            anchor: '100%'
        },

        items: [{
            xtype: 'container',
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
                name: 'chave',
                allowBlank: false,
                store: ['administrador', 'funcionário']
            }, {
                fieldLabel: 'Senha',
                emptyText: 'senha',
                name: 'senha'
            }]
        }]
    }],
    buttons: [{
        text: 'Cancelar',
        handler: function (btn) {
            btn.up('cadastrar-usuario').destroy();
        }
    },{
        text: 'Cadastrar',
        disabled: true,
        formBind: true,
        itemId: 'cadastrarusuario',
        dataIndex: 'cadastrarusuario',
        handler: 'cadastrar'
    }]
});