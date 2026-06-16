Ext.define('ProjSistemaOs.view.usuario.RedefinirSenhaWindow', {
    extend: 'Ext.window.Window',
    alias: 'widget.redefinir-senha',
    token: null,
    title: 'Redefinir Senha',
    width: 450,
    modal: true,
    resizable: false,
    layout: 'fit',

    // ADICIONADO: Captura o token da URL assim que a janela é inicializada
    initComponent: function() {
        var me = this;
        var url = window.location.href;
        
        // Lógica robusta para pegar o token mesmo com '#' na URL
        if (url.indexOf('token=') !== -1) {
            me.token = url.split('token=')[1].split('&')[0].split('?')[0];
        }

        me.callParent(arguments);
    },

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
                Ext.Msg.alert('Erro', 'As senhas não coincidem.');
                return;
            }

            // Verifica se o token foi capturado antes de enviar
            if (!win.token) {
                Ext.Msg.alert('Erro', 'Token de recuperação não encontrado na URL.');
                return;
            }

            Ext.Ajax.request({
                url: sistemaOsLocal.apiUrl + '/auth/resetar-senha',
                method: 'POST',
                jsonData: {
                    token: win.token, // Agora o win.token terá o valor da URL
                    novaSenha: valores.novaSenha
                },
                success: function() {
                    Ext.Msg.alert('Sucesso', 'Senha alterada com sucesso!', function() {
                        win.close();
                        window.location.href = 'http://localhost:8080/#login'; // Redireciona para o login
                    } );
                },
                failure: function() {
                    Ext.Msg.alert('Erro', 'Token inválido ou expirado.');
                }
            });
        }
    }, {
        text: 'Cancelar',
        handler: function(btn) {
            btn.up('window').close();
        }
    }]
});