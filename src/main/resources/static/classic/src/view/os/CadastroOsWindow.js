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
                    xtype: 'textfield',
                    margin: '0 10 0 0',
                    flex: 4
                }, {
                    xtype: 'numberfield',
                    margin: '0 10 0 0',
                    flex: 1
                }, {
                    xtype: 'button',
                    iconCls: 'fa fa-plus'
                }]
            }, {
                xtype: 'grid',
                title: 'Peças',
                ui: 'light',
                border: !0,
                columnLines: !0,
                scrollable: 'y',
                minHeight: 150,
                disableSelection: !0,
                enableColumnHide: !1,
                enableColumnMove: !1,
                enableColumnResize: !1,
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