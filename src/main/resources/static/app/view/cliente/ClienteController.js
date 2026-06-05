Ext.define('ProjSistemaOs.view.cliente.ClienteController', {
	extend: 'Ext.app.ViewController',
    alias: 'controller.cliente-controller',

	requires: [
		'ProjSistemaOs.util.MensagemUtil',
		'ProjSistemaOs.util.Config'
	],

	onValidaFormulario: function (form, valid) {
		this.lookup('btnSalvar').setDisabled(!valid);
	},

	fecharJanela: function() {
		var me = this, vw = me.getView();
		if (vw && !vw.destroyed && !vw.isDestroying) {
			vw.close();
		}
	},
	salvarCliente: function() {
		var me = this, vw = me.getView(),
		form = vw.down('form').getForm().getValues();

		var dados = {
			nome: form.nome,
			telefone: form.telefone,
			status: "ATIVO",
			endereco: {
				rua: form.rua,
				numero: form.numero,
				logradouro: form.logradouro,
				complemento: form.complemento
			}
		}

		Ext.Ajax.request({
			url: sistemaOsLocal.apiUrl + '/cliente/cadastrar',
			method: 'POST',
			jsonData: JSON.stringify(dados),
			success: function (conn, response, options, eOpts) {
				let r = Ext.JSON.decode(conn.responseText, true);
				if (r && r.resposta.sucesso) {
					vw.fireEvent('clientesalvo'); //Dispara o evento quando salva o cliente.
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
});