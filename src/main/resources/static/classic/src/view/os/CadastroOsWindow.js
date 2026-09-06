Ext.define('ProjSistemaOs.view.os.CadastroOsWindow', {
    extend: 'Ext.form.Panel',
    xtype: 'cadastro-os-panel',

    requires: [
        'ProjSistemaOs.view.cliente.ClienteWindow',
        'ProjSistemaOs.view.ux.TagFieldHtmlLabel'
    ],

    controller: {
        init: function () {
            var grid = this.lookupReference('gridPecas');
            grid.getStore().on('datachanged', this.atualizarTotalOrcamento, this);
        },
        adicionarCliente: function(){
            Ext.create('ProjSistemaOs.view.cliente.ClienteWindow', {
                floating: true,
                modal: true,
                iconCls: 'fa fa-plus',
            }).show();
        },
        adicionarPecaGrid: function () {
            var view = this.getView(),
            combo = view.lookupReference('comboPeca'),
            qtdField = view.lookupReference('qtdPeca'),
            grid = view.lookupReference('gridPecas');

            var records = combo.getValueRecords();
            var quantidade = qtdField.getValue();

            if (Ext.isEmpty(records)) {
                Ext.Msg.alert('Atenção', 'Selecione a peça.');
                return;
            }
            if (Ext.isEmpty(quantidade)) {
                Ext.Msg.alert("Atenção", "Selecione a quantidade");
                return;
            }

            var record = records[0];
            var preco = record.get('preco');

            grid.getStore().add({
                pecaId: record.get('id'),
                nome: record.get('nome'),
                preco: preco,
                quantidade: quantidade,
                valorTotal: preco * quantidade,
            });

            combo.setValue(null);
            qtdField.setValue(1);
        },

        removerPecaGrid: function(grid, rowIndex) {
            grid.getStore().removeAt(rowIndex);
        },
        atualizarTotalOrcamento: function () {
            var me = this, view = me.getView(),
                grid = view.lookupReference('gridPecas'),
                orcamentoTotal = view.lookupReference('orcamentoTotal'),
                maoDeObra = view.lookupReference('maoDeObra');

            var totalPecas = 0;
            grid.getStore().each(function(rec) {
                totalPecas += rec.get('valorTotal');
            });

            orcamentoTotal.setValue(totalPecas + maoDeObra.getValue() || 0);
        },
        cadastrarOs: function () {
            var me = this, vw = me.getView(),
                values = vw.getForm().getValues(),
                grid = vw.lookupReference('gridPecas'),
                itens = [];
            grid.getStore().each(function(rec) {
                itens.push({
                    pecaId: rec.get('pecaId'),
                    quantidade: rec.get('quantidade'),
                    valorUnitario: rec.get('preco')
                });
            });

            var playload = {
                clienteId: values.cliente,
                modelo: values.modelo,
                cor: values.cor,
                orcamento: {
                    valorMaoDeObra: values.maoDeObra,
                    observacoes: values.observacoes,
                    itens: itens,
                }
            };

            Ext.Ajax.request({
                url: sistemaOsLocal.apiUrl + '/usuarios/adicionar',
                method: 'POST',
                data: playload,
                success: function (conn, response, options, eOpts) {
                    let r = Ext.JSON.decode(conn.responseText, true);
                    if (r) {
                        vw.fireEvent('ossalva');
                        vw.close();
                    }
                },
                failure: function (conn, response, options, eOpts) {
                    Avisos.mostrarServidorIndisponivel();
                }
            });
        }
    },

    title: 'Cadastro Os',
    layout: {
        type: 'vbox',
        align: 'stretch'
    },
    resizable: false,
    width: 800,
    height: 600,
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
            xtype: 'tagfieldhtmllabel',
            fieldLabel: 'Cliente',
            name: 'cliente',
            flex: 4,
            margin: '0 10 0 0',
            minChars: 0,
            autoSelect: false,
            valueField: 'id',
            autoSelectLast: false,
            queryMode: 'remote',
            queryParam: 'nome',
            pageSize: 25,
            multiSelect: false,
            listConfig: {
                itemTpl: [
                    '<i class="fa fa-user" style="color:#90D5FF;"></i> {nome:htmlEncode}',
                    '<div>Telefone: {telefone:htmlEncode}</div>',
                    '</div>'
                ]
            },
            labelTpl: [
                '<div style="font-size:12px;">',
                    '<i class="fa fa-user" style="color:#90D5FF;"></i> {nome:htmlEncode} - {telefone:htmlEncode}',
                '</div>',
            ],
            store: {
                fields: [{
                    name: 'id',
                    type: 'int',
                }, {
                    name: 'nome',
                    type: 'string'
                }, {
                    name: 'telefone',
                    type: 'string'
                }],
                proxy: {
                    type: 'ajax',
                    url: window.location.origin + '/cliente/listar/os',
                    method: 'GET',
                    reader: {
                        type: 'json',
                        rootProperty: 'listaClientes',
                        totalProperty: 'total'
                    }
                },
                pageSize: 25,
                autoLoad: false,
                autoDestroy: true
            },
            maxLength: 80
        }, {
            xtype: 'button',
            iconCls: 'fa fa-user-plus',
            tooltip: 'Adicionar cliente',
            margin: '29 0 0 10',
            ui: 'default-toolbar',
            handler: 'adicionarCliente'
        }]
    }, {
        xtype: 'container',
        layout: 'hbox',
        margin: '10 0 0 0',
        items: [{
            xtype: 'textfield',
            name: 'modelo',
            fieldLabel: 'Modelo/Marca',
            flex: 3,
            margin: '0 10 0 0'
        }, {
            xtype: 'textfield',
            name: 'cor',
            fieldLabel: 'Cor',
            flex: 2
        }, {
            xtype: 'numberfield',
            name: 'maoDeObra',
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
            iconCls: 'fa fa-cog',
            title: 'Adicionar peças',
            scrollable: 'y',
            bodyPadding: '15',
            layout: {
                type: 'vbox',
                align: 'stretch'
            },
            items: [{
                xtype: 'container',
                layout: 'hbox',
                margin: '0 0 10 0',
                items: [{
                    xtype: 'tagfieldhtmllabel',
                    margin: '0 10 0 0',
                    flex: 4,
                    minChars: 0,
                    autoSelect: false,
                    autoSelectLast: false,
                    reference: 'comboPeca',
                    valueField: 'id',
                    queryMode: 'remote',
                    queryParam: 'descricao',
                    multiSelect: false,
                    pageSize: 25,
                    listConfig: {
                        itemTpl: [
                            '<i class="fa fa-screwdriver" style="color:#90D5FF;"></i> {nome:htmlEncode}',
                            '<div>Valor/Unidade: R${preco:number("0,000.00##")}</div>',
                            '</div>'
                        ]
                    },
                    labelTpl: [
                        '<div style="font-size:12px;">',
                        '<i class="fa fa-screwdriver" style="color:#90D5FF;"></i> {nome:htmlEncode} - R${preco:number("0,000.00##")}',
                        '</div>',
                    ],
                    store: {
                        fields: [{
                            name: 'id',
                            type: 'int',
                        }, {
                            name: 'nome',
                            type: 'string'
                        }, {
                            name: 'preco',
                            type: 'float'
                        }],
                        proxy: {
                            type: 'ajax',
                            url: window.location.origin + '/peca/listar/os',
                            reader: {
                                type: 'json',
                                rootProperty: 'listaEstoque',
                                totalProperty: 'total'
                            }
                        },
                        pageSize: 25,
                        autoLoad: false,
                        autoDestroy: true
                    },
                    maxLength: 80
                }, {
                    xtype: 'numberfield',
                    reference: 'qtdPeca',
                    flex: 1,
                    margin: '0 10 0 0',
                    minValue: 1,
                    value: 1
                }, {
                    xtype: 'button',
                    iconCls: 'fa fa-plus',
                    handler: 'adicionarPecaGrid'
                }]
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
                }, {
                    xtype: 'actioncolumn',
                    width: 40,
                    align: 'center',
                    items: [{
                        iconCls: 'fa fa-trash',
                        tooltip: 'Remover',
                        handler: 'removerPecaGrid'
                    }]
                }]
            }]
        }]
    }, {
        xtype: 'textarea',
        name: 'observacoes',
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
        text: 'Cancelar',
        iconCls: 'fa fa-times',
        handler: function (btn) {
            btn.up('cadastro-os-panel').destroy();
        },
    }, {
        text: 'Cadastar',
        iconCls: 'fa fa-check',
        handler: 'cadastrarOs'
    }]
});