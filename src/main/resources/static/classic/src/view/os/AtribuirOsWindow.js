Ext.define('ProjSistemaOs.view.os.AtribuirOsWindow', {
    extend: 'Ext.panel.Panel',
    xtype: 'atribuir-os-window',

    requires: [
        'ProjSistemaOs.util.Sessao',
        'ProjSistemaOs.util.OsOperacional'
    ],

    osId: null,
    responsavelIdAtual: null,
    responsavelNomeAtual: null,

    controller: {
        init: function () {
            var view = this.getView(), combo = view.lookupReference('responsavel');
            if (!Sessao.isAdm()) {
                view.close();
                Ext.Msg.alert('Atenção', 'Somente administradores podem atribuir ou reatribuir uma OS.');
                return;
            }
            OsOperacional.carregarResponsaveis(function (rows, success) {
                if (!success || view.destroyed) {
                    return;
                }
                combo.getStore().loadData(rows || []);
                if (view.responsavelIdAtual !== null && view.responsavelIdAtual !== undefined &&
                    !combo.getStore().findRecord('id', view.responsavelIdAtual, 0, false, true, true)) {
                    combo.getStore().add({ id: view.responsavelIdAtual, nome: view.responsavelNomeAtual || ('Usuário #' + view.responsavelIdAtual) });
                }
                combo.setValue(view.responsavelIdAtual);
            }, this);
        },

        salvarAtribuicao: function () {
            var view = this.getView(), combo = view.lookupReference('responsavel'), id = combo.getValue();
            if (!Sessao.isAdm()) {
                Ext.Msg.alert('Atenção', 'Somente administradores podem atribuir ou reatribuir uma OS.');
                return;
            }
            if (!view.osId || id === null || id === undefined || id === '') {
                Ext.Msg.alert('Atenção', 'Selecione um responsável ativo.');
                return;
            }
            if (String(id) === String(view.responsavelIdAtual)) {
                view.close();
                return;
            }
            Ext.Ajax.request({
                url: OsOperacional.apiUrl('/os/' + encodeURIComponent(view.osId) + '/responsavel'),
                method: 'PUT',
                jsonData: { responsavelId: Number(id) },
                callback: function (options, success, response) {
                    if (success) {
                        view.fireEvent('ossalva', Ext.decode(response.responseText, true));
                        view.close();
                    } else {
                        OsOperacional.exibirErro(response, 'Não foi possível atribuir o responsável.');
                    }
                }
            });
        }
    },

    title: 'Atribuir responsável',

    // Comportamento que antes vinha do Ext.window.Window
    modal: true,
    floating: true,
    closable: true,
    closeAction: 'destroy',
    draggable: true,
    resizable: false,

    scrollable: 'y',
    bodyPadding: 15,
    ui: 'light',
    padding: 5,
    shadow: true,
    width: 430,
    layout: 'fit',
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
        reference: 'form',
        defaults: { anchor: '100%', labelAlign: 'top' },
        items: [{
            xtype: 'combobox',
            reference: 'responsavel',
            name: 'responsavelId',
            fieldLabel: 'Responsável',
            emptyText: 'Selecione um usuário ativo',
            queryMode: 'local',
            valueField: 'id',
            displayField: 'nome',
            forceSelection: true,
            editable: false,
            allowBlank: false,
            store: { fields: ['id', 'nome'], data: [] },
            listConfig: { itemTpl: '{nome:htmlEncode}' }
        }]
    }],
    buttons: [{
        text: 'Cancelar',
        handler: function (button) { button.up('atribuir-os-window').close(); }
    }, {
        text: 'Salvar',
        iconCls: 'fa fa-check',
        handler: 'salvarAtribuicao'
    }]
});