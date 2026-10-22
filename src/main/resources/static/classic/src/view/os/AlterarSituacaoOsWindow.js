Ext.define('ProjSistemaOs.view.os.AlterarSituacaoOsWindow', {
    extend: 'Ext.window.Window',
    xtype: 'alterar-situacao-os-window',

    requires: ['ProjSistemaOs.util.OsOperacional'],

    osId: null,
    situacaoAtual: null,

    controller: {
        init: function () {
            var view = this.getView(), combo = view.lookupReference('situacao');
            combo.setValue(OsOperacional.normalizarSituacao(view.situacaoAtual) || 'PENDENTE');
        },

        salvarSituacao: function () {
            var view = this.getView(), combo = view.lookupReference('situacao'), situacao = combo.getValue();
            if (!view.osId || !situacao) {
                Ext.Msg.alert('Atenção', 'Selecione uma situação válida.');
                return;
            }
            if (OsOperacional.estaEncerrada({ situacao: view.situacaoAtual })) {
                Ext.Msg.alert('Atenção', 'A situação de uma OS encerrada não pode ser alterada. Reabra a OS antes.');
                return;
            }
            if (situacao === OsOperacional.normalizarSituacao(view.situacaoAtual)) {
                view.close();
                return;
            }
            Ext.Ajax.request({
                url: OsOperacional.apiUrl('/os/' + encodeURIComponent(view.osId) + '/situacao'),
                method: 'PUT',
                jsonData: { situacao: situacao },
                callback: function (options, success, response) {
                    if (success) {
                        view.fireEvent('ossalva', Ext.decode(response.responseText, true));
                        view.close();
                    } else {
                        OsOperacional.exibirErro(response, 'Não foi possível alterar a situação.');
                    }
                }
            });
        }
    },

    title: 'Alterar situação da OS',
    modal: true,
    floating: true,
    width: 430,
    bodyPadding: 16,
    resizable: false,
    layout: 'fit',
    items: [{
        xtype: 'form',
        defaults: { anchor: '100%', labelAlign: 'top' },
        items: [{
            xtype: 'combobox',
            reference: 'situacao',
            name: 'situacao',
            fieldLabel: 'Situação',
            queryMode: 'local',
            valueField: 'value',
            displayField: 'text',
            forceSelection: true,
            editable: false,
            allowBlank: false,
            store: {
                fields: ['value', 'text'],
                data: [
                    { value: 'PENDENTE', text: 'Pendente' },
                    { value: 'EM_ANDAMENTO', text: 'Em Andamento' },
                    { value: 'CONCLUIDO', text: 'Concluído' },
                    { value: 'CANCELADO', text: 'Cancelado' }
                ]
            }
        }]
    }],
    buttons: [{ text: 'Cancelar', handler: function (button) { button.up('window').close(); } }, {
        text: 'Salvar',
        iconCls: 'fa fa-check',
        handler: 'salvarSituacao'
    }]
});
