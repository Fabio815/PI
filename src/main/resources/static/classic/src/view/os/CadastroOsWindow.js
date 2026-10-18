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
        formatarMoeda: function (valor) {
            return new Intl.NumberFormat('pt-BR', {
                style: 'currency',
                currency: 'BRL',
                minimumFractionDigits: 2,
                maximumFractionDigits: 2
            }).format(valor || 0);
        },
        converterMoedaParaNumero: function (valor) {
            if (Ext.isNumber(valor)) {
                return valor;
            }

            var digitos = String(valor || '').replace(/\D/g, '');
            return digitos ? parseInt(digitos, 10) / 100 : 0;
        },
        formatarMaoDeObra: function (campo, novoValor, valorAnterior) {
            if (campo.formatandoMoeda) {
                return;
            }

            if (String(novoValor || '').indexOf('-') !== -1) {
                campo.formatandoMoeda = true;
                campo.setValue(valorAnterior || 'R$ 0,00');
                campo.formatandoMoeda = false;
                campo.markInvalid('Valores negativos não são permitidos.');
                return;
            }

            var valorNumerico = this.converterMoedaParaNumero(novoValor);
            var valorFormatado = this.formatarMoeda(valorNumerico);

            if (novoValor !== valorFormatado) {
                campo.formatandoMoeda = true;
                campo.setValue(valorFormatado);
                campo.formatandoMoeda = false;
            }

            campo.clearInvalid();
            this.atualizarTotalOrcamento();
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
            var quantidade = Number(qtdField.getValue());

            if (Ext.isEmpty(records)) {
                Ext.Msg.alert('Atenção', 'Selecione a peça.');
                return;
            }
            if (!Number.isInteger(quantidade) || quantidade < 1) {
                Ext.Msg.alert('Atenção', 'Informe uma quantidade inteira maior que zero.');
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

            var valorMaoDeObra = me.converterMoedaParaNumero(maoDeObra.getValue());
            orcamentoTotal.setValue(me.formatarMoeda(totalPecas + valorMaoDeObra));
        },
        cadastrarOs: function () {
            var me = this, vw = me.getView(),
                values = vw.getForm().getValues(),
                comboCliente = vw.lookupReference('comboCliente'),
                grid = vw.lookupReference('gridPecas'),
                itens = [];

            if (!vw.getForm().isValid()) {
                Ext.Msg.alert('Atenção', 'Corrija os campos inválidos antes de cadastrar a OS.');
                return;
            }

            grid.getStore().each(function(rec) {
                itens.push({
                    pecaId: rec.get('pecaId'),
                    quantidade: rec.get('quantidade'),
                    valorUnitario: rec.get('preco')
                });
            });

            var clienteRecords = comboCliente.getValueRecords();
            if (Ext.isEmpty(clienteRecords)) {
                Ext.Msg.alert('Atenção', 'Selecione um cliente.');
                return;
            }
            var clienteId = clienteRecords[0].get('id');

            var payload = {
                clienteId: clienteId,
                modelo: values.modelo,
                cor: values.cor,
                cor: values.cor,
                modelo: values.modelo,
                orcamento: {
                    valorServico: me.converterMoedaParaNumero(values.maoDeObra),
                    observacoes: values.observacoes,
                    itens: itens
                }
            };

            Ext.Ajax.request({
                url: sistemaOsLocal.apiUrl + '/os/cadastrar',
                method: 'POST',
                jsonData: payload,
                success: function (conn, response, options, eOpts) {
                    let r = Ext.JSON.decode(conn.responseText, true);
                    if (r) {
                        vw.fireEvent('ossalva');
                        vw.close();

                        if (r.avisosEstoque && r.avisosEstoque.length > 0) {
                            Avisos.mensagemAviso(
                                'OS cadastrada com sucesso.<br><br>' +
                                Ext.Array.map(r.avisosEstoque, function (aviso) {
                                    return Ext.String.htmlEncode(aviso);
                                }).join('<br>')
                            );
                        }
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
            reference: 'comboCliente',
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
            xtype: 'textfield',
            name: 'maoDeObra',
            reference: 'maoDeObra',
            fieldLabel: 'Mão de obra (R$)',
            value: 'R$ 0,00',
            allowBlank: false,
            maskRe: /[0-9]/,
            fieldStyle: 'text-align:right;font-variant-numeric:tabular-nums;',
            listeners: {
                change: 'formatarMaoDeObra'
            },
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
                            '<div>Valor/Unidade: {[this.formatarMoeda(values.preco)]}</div>',
                            '</div>',
                            {
                                formatarMoeda: function (valor) {
                                    return new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' }).format(valor || 0);
                                }
                            }
                        ]
                    },
                    labelTpl: [
                        '<div style="font-size:12px;">',
                        '<i class="fa fa-screwdriver" style="color:#90D5FF;"></i> {nome:htmlEncode} - {[this.formatarMoeda(values.preco)]}',
                        '</div>',
                        {
                            formatarMoeda: function (valor) {
                                return new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' }).format(valor || 0);
                            }
                        }
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
                    fieldLabel: 'Quantidade',
                    flex: 1,
                    margin: '0 10 0 0',
                    minValue: 1,
                    allowDecimals: false,
                    allowExponential: false,
                    allowBlank: false,
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
                },{
                    text: 'Preco Unitário',
                    dataIndex: 'preco',
                    align: 'right',
                    renderer: function (value, metaData) {
                        metaData.style = 'text-align:right;font-variant-numeric:tabular-nums;';
                        return Ext.isNumber(value) ? new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' }).format(value) : value;
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
                        return Ext.isNumber(value) ? new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' }).format(value) : value;
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
            xtype: 'textfield',
            name: 'orcamento',
            reference: 'orcamentoTotal',
            fieldLabel: 'Orçamento (R$)',
            value: 'R$ 0,00',
            fieldStyle: 'text-align:right;font-variant-numeric:tabular-nums;',
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