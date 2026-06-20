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