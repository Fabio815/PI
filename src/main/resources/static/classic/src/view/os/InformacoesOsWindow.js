Ext.define('ProjSistemaOs.view.os.InformacoesOsWindow', {
    extend: 'Ext.form.Panel',
    xtype: 'informacoes-os-panel',

    requires: [
        'ProjSistemaOs.view.cliente.ClienteWindow',
        'ProjSistemaOs.view.ux.TagFieldHtmlLabel'
    ],

    controller: {

    },

    title: 'Os',
    layout: {
        type: 'vbox',
        align: 'stretch'
    },
    resizable: false,
    width: 800,
    height: 700,
    scrollable: 'y',
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
    fieldDefaults: {
        labelAlign: 'top',
        msgTarget: 'side'
    },

    items: [{
        xtype: 'textfield',
        fieldLabel: 'Cliente',
        reference: 'clienteDisplay',
        readOnly: true
    }, {
        xtype: 'container',
        layout: 'hbox',
        margin: '10 0 0 0',
        items: [{
            xtype: 'textfield',
            name: 'modelo',
            fieldLabel: 'Modelo/Marca',
            readOnly: true,
            flex: 3,
            margin: '0 10 0 0'
        }, {
            xtype: 'textfield',
            name: 'cor',
            readOnly: true,
            fieldLabel: 'Cor',
            flex: 2
        }, {
            xtype: 'numberfield',
            name: 'maoDeObra',
            readOnly: true,
            reference: 'maoDeObra',
            fieldLabel: 'Mão de obra',
            margin: '0 0 0 10'
        }]
    }, {
        xtype: 'panel',
        title: '',
        margin: '10 0 0 0',
        floating: false,
        modal: true,
        width: '80%',
        padding: 5,
        style: {
            backgroundColor: '#efefef'
        },
        layout: 'fit',
        items: [{
            ui: 'light',
            xtype: 'form',
            scrollable: 'y',
            bodyPadding: '15',
            layout: {
                type: 'vbox',
                align: 'stretch'
            },
            items: [{
                xtype: 'container',
                layout: 'hbox',
                margin: '0 0 10 0'
            }, {
                xtype: 'grid',
                title: 'Peças',
                reference: 'gridPecas',
                ui: 'light',
                border: true,
                columnLines: true,
                scrollable: 'y',
                minHeight: 200,
                maxHeight: 200,
                disableSelection: true,
                enableColumnHide: false,
                enableColumnMove: false,
                enableColumnResize: false,
                store: {
                    fields: [{
                        name: 'pecaId',
                        type: 'int'
                    }, {
                        name: 'nome',
                        type: 'string'
                    }, {
                        name: 'preco',
                        type: 'float'
                    }, {
                        name: 'quantidade',
                        type: 'int'
                    }, {
                        name: 'valorTotal',
                        type: 'float'
                    }],
                    data: [],
                },
                columns: [{
                    text: 'Nome',
                    dataIndex: 'nome',
                    flex: 4
                }, {
                    text: 'Preco Unitário',
                    dataIndex: 'preco',
                    renderer: function (value) {
                        return Ext.util.Format.currency(value, 'R$ ', 2, false);
                    },
                    flex: 2
                }, {
                    text: 'Total',
                    dataIndex: 'valorTotal',
                    flex: 2,
                    renderer: function (value) {
                        return Ext.util.Format.currency(value, 'R$ ', 2, false);
                    },
                }]
            }]
        }]
    }, {
        xtype: 'textarea',
        name: 'observacoes',
        readOnly: true,
        fieldLabel: 'Observações',
    }, {
        xtype: 'container',
        layout: 'hbox',
        items: [{
            xtype: 'numberfield',
            name: 'orcamento',
            reference: 'orcamentoTotal',
            fieldLabel: 'Orçamento',
            width: 150,
            readOnly: true
        }]
    }],

    buttons: [{
        text: 'Fechar',
        iconCls: 'fa fa-times',
        handler: function (btn) {
            btn.up('informacoes-os-panel').destroy();
        },
    }]
});