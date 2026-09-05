Ext.define('ProjSistemaOs.view.os.CadastroOsWindow', {
    extend: 'Ext.form.Panel',
    xtype: 'cadastro-os-panel',

    requires: [
        'ProjSistemaOs.view.cliente.ClienteWindow',
        'ProjSistemaOs.view.ux.TagFieldHtmlLabel'
    ],

    controller: {
        adicionarCliente: function(){
            Ext.create('ProjSistemaOs.view.cliente.ClienteWindow', {
                floating: true,
                modal: true,
                iconCls: 'fa fa-plus',
            }).show();
        },
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
            fieldLabel: 'Modelo/Marca',
            flex: 3,
            margin: '0 10 0 0'
        }, {
            xtype: 'textfield',
            fieldLabel: 'Cor',
            flex: 2
        }, {
            xtype: 'numberfield',
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
                            '<i class="fa fa-screwdriver" style="color:#90D5FF;"></i> {nome:htmlEncode} - {preco:htmlEncode}',
                            '<div>Valor/Unidade: R${preco:number("0,000.00##")}</div>',
                            '</div>'
                        ]
                    },
                    labelTpl: [
                        '<div style="font-size:12px;">',
                        '<i class="fa fa-screwdriver" style="color:#90D5FF;"></i> {nome:htmlEncode} - {preco:htmlEncode}',
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
                    referece: 'qtdPeca',
                    flex: 1,
                    margin: '0 10 0 0',
                    minValue: 1,
                    value: 1
                }, {
                    xtype: 'button',
                    iconCls: 'fa fa-plus',
                    handler: 'adicionarCliente'
                }]
            }, {
                xtype: 'grid',
                title: 'Peças',
                reference: 'gridPecas',
                ui: 'light',
                border: true,
                columnLines: true,
                scrollable: 'y',
                minHeight: 150,
                disableSelection: true,
                enableColumnHide: false,
                enableColumnMove: false,
                enableColumnResize: false,
                store: {
                    fields: [{
                        name: 'pecaId', type: 'int'
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
                        name: 'valoTotal',
                        type: 'float'
                    }],
                    data: []
                },
                columns: [{
                    text: 'Nome',
                    dataIndex: 'nome',
                    flex: 3
                }, {
                    text: 'Preco',
                    dataIndex: 'preco',
                    renderer: function (value) {
                        return 'R$ ' + Ext.util.Format.number(value, '0,000.00');
                    },
                    flex: 2
                }, {
                    xtype: 'actioncolumn',
                    width: 40,
                    align: 'center',
                    items: [{
                        iconCls: 'fa fa-check',
                        tooltip: 'Remover',
                        handler: 'removerPecaGrid'
                    }]
                }]
            }]
        }]
    }, {
        xtype: 'textarea',
        fieldLabel: 'Observações',
    }, {
        xtype: 'container',
        layout: 'hbox',
        items: [{
            xtype: 'numberfield',
            fieldLabel: 'Orçamento',
            width: 150
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
        iconCls: 'fa fa-check'
    }]
});