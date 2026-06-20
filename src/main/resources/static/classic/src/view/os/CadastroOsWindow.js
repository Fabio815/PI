Ext.define('ProjSistemaOs.view.os.CadastroOsWindow', {
    extend: 'Ext.form.Panel',
    xtype: 'cadastro-os-panel',

    title: 'Cadastro Os',
    layout: 'fit',
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
        defaults: {
            border: false,
            flex: 1,
            layout: 'anchor',
            padding: 5,
        },

        layout: 'hbox',
        items: [{
            items: [{
                xtype: 'textfield',
                fieldLabel: 'First Name',
                anchor: '-5',
                name: 'first'
            }, {
                xtype: 'textfield',
                fieldLabel: 'First Name',
                anchor: '-5',
                name: 'first'
            }]
        }, {
            items: [{
                xtype: 'textfield',
                fieldLabel: 'Last Name',
                anchor: '100%',
                name: 'last'
            }, {
                xtype: 'textfield',
                fieldLabel: 'Email',
                anchor: '100%',
                name: 'email',
                vtype: 'email'
            }]
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
    }]
});