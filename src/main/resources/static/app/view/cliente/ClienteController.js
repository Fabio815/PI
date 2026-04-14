Ext.define('ProjSistemaOs.view.cliente.ClienteController', {
	extend: 'Ext.app.ViewController',
	alias: 'controller.cliente-controller',

	requires: [
		'ProjSistemaOs.util.MensagemUtil'
	],

	fecharJanela: function() {
		var me = this, vw = me.getView();
		if (vw && !vw.destroyed && !vw.isDestroying) {
			vw.close();
		}
	},
	salvarCliente: function() {
		var me = this, vw = me.getView(),
			form = vw.down('form').getForm().getValues();
		Ext.Ajax.request({
			url: 'http://localhost:8080/sistema-os/api/cliente/cadastrar',
			method: 'POST',
			jsonData: form,
			callback: function(options, success, response) {
				if (vw && !vw.destroyed && !vw.isDestroying) {
					var r = Ext.decode(response.responseText, true);
					if (r && r.sucesso) {
						vw.fireEvent('clientesalvo'); //Dispara o evento quando salva o cliente.
						Avisos.mensagemSucesso(r.mensagem);
						vw.close();
					} else if (r) {
						Avisos.mensagemAviso(r.mensagem);
					} else {
						Avisos.mostrarServidorIndisponivel();
					}
				}
			}
		});
	}
});