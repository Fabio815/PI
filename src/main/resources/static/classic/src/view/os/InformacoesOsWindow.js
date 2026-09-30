Ext.define('ProjSistemaOs.view.os.InformacoesOsWindow', {
    extend: 'Ext.form.Panel',
    xtype: 'informacoes-os-panel',

    osId: null,

    controller: {
        init: function () {
            var view = this.getView();
            if (view.osId) {
                this.carregarOs(view.osId);
            }
        },

        carregarOs: function (id) {
            var view = this.getView();
            Ext.Ajax.request({
                url: sistemaOsLocal.apiUrl + '/os/' + id,
                method: 'GET',
                success: function (response) {
                    var os = Ext.JSON.decode(response.responseText, true);
                    var formatadorMoeda = new Intl.NumberFormat('pt-BR', {
                        style: 'currency',
                        currency: 'BRL',
                        minimumFractionDigits: 2,
                        maximumFractionDigits: 2
                    });

                    view.getForm().setValues({
                        modelo: os.modelo,
                        cor: os.cor,
                        maoDeObra: formatadorMoeda.format(os.orcamento.valorServico || 0),
                        observacoes: os.orcamento.observacoes,
                        orcamento: formatadorMoeda.format(os.orcamento.valorTotal || 0)
                    });

                    var clienteDisplay = view.lookupReference('clienteDisplay');
                    clienteDisplay.setValue(os.cliente.nome + ' - ' + os.cliente.telefone);

                    var grid = view.lookupReference('gridPecas');
                    var dadosGrid = os.orcamento.itens.map(function (item) {
                        return {
                            pecaId: item.peca.id,
                            nome: item.peca.nome,
                            preco: item.valorUnitario,
                            quantidade: item.quantidade,
                            valorTotal: item.valorTotal
                        };
                    });
                    grid.getStore().loadData(dadosGrid);
                },
                failure: function () {
                    Avisos.mostrarServidorIndisponivel();
                }
            });
        }
    },

    title: 'Os',
    iconCls: 'fa fa-eye',
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
        xtype: 'container',
        layout: 'hbox',
        items: [{
            xtype: 'textfield',
            fieldLabel: 'Cliente',
            reference: 'clienteDisplay',
            readOnly: true,
            flex: 4
        }]
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
            fieldLabel: 'Cor',
            readOnly: true,
            flex: 2
        }, {
            xtype: 'textfield',
            name: 'maoDeObra',
            reference: 'maoDeObra',
            fieldLabel: 'Mão de obra (R$)',
            readOnly: true,
            fieldStyle: 'text-align:right;font-variant-numeric:tabular-nums;',
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
                    data: []
                },
                columns: [{
                    text: 'Nome',
                    dataIndex: 'nome',
                    flex: 4
                }, {
                    text: 'Preco Unitário',
                    dataIndex: 'preco',
                    align: 'right',
                    renderer: function (value, metaData) {
                        metaData.style = 'text-align:right;font-variant-numeric:tabular-nums;';
                        return new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' }).format(value || 0);
                    },
                    flex: 2
                }, {
                    text: 'Quantidade',
                    dataIndex: 'quantidade',
                    flex: 2
                }, {
                    text: 'Total',
                    dataIndex: 'valorTotal',
                    flex: 2,
                    align: 'right',
                    renderer: function (value, metaData) {
                        metaData.style = 'text-align:right;font-variant-numeric:tabular-nums;';
                        return new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' }).format(value || 0);
                    }
                }]
            }]
        }]
    }, {
        xtype: 'textarea',
        name: 'observacoes',
        fieldLabel: 'Observações',
        readOnly: true
    }, {
        xtype: 'container',
        layout: 'hbox',
        items: [{
            xtype: 'textfield',
            name: 'orcamento',
            reference: 'orcamentoTotal',
            fieldLabel: 'Orçamento (R$)',
            width: 150,
            fieldStyle: 'text-align:right;font-variant-numeric:tabular-nums;',
            readOnly: true
        }]
    }],

    buttons: [{
        text: 'Fechar',
        iconCls: 'fa fa-times',
        handler: function (btn) {
            btn.up('informacoes-os-panel').destroy();
        }
    }]
});