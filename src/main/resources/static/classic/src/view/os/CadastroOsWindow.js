Ext.define('ProjSistemaOs.view.os.CadastroOsWindow', {
    extend: 'Ext.form.Panel',
    xtype: 'cadastro-os-panel',

    requires: [
        'ProjSistemaOs.view.cliente.ClienteWindow'
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
            xtype: 'combobox',
            fieldLabel: 'Telefone',
            name: 'telefone',
            flex: 3,
            margin: '0 10 0 0'
        }, {
            xtype: 'textfield',
            fieldLabel: 'Nome',
            name: 'nome',
            flex: 5
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
        xtype: 'grid',
        padding: 10,
        reference: 'gridPecas',
        title: 'Peças',
        height: 250,
        ui: 'light',
        shadow: true,
        tbar: [{
            xtype: 'button',
            tooltip: 'Adicionar peça',
            iconCls: 'fa fa-plus',
            handler: 'adicionarPeca'
        }],
        store: {
            fields: [
                'id',
                'descricao',
                'quantidade',
                'valorUnitario'
            ],
            data: [
                { id: 1, descricao: 'Corrente', quantidade: 1, valorUnitario: 250.00 },
                { id: 2, descricao: 'Pneu', quantidade: 2, valorUnitario: 89.90 },
                { id: 3, descricao: 'Aro 29', quantidade: 1, valorUnitario: 45.00 },
                { id: 4, descricao: 'Marcha', quantidade: 1, valorUnitario: 30.00 }
            ]
        },

        columns: [{
            text: 'Código',
            dataIndex: 'id',
            flex: 1,
        }, {
            text: 'Peça',
            dataIndex: 'descricao',
            flex: 5
        }, {
            text: 'Qtd',
            dataIndex: 'quantidade',
            flex: 1
        }, {
            text: 'Valor unitário',
            dataIndex: 'valorUnitario',
            flex: 2
        }, {
            xtype: 'actioncolumn',
            width: 80,
            text: 'Remover',
            items: [{
                iconCls: 'fa fa-times',
                tooltip: 'Remover',
                handler: function (grid, rowIndex) {
                    grid.getStore().removeAt(rowIndex);
                }
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