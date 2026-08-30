Ext.define('ProjSistemaOs.view.estoque.EstoqueWindow', {
    extend: 'Ext.panel.Panel',
    // alias: 'widget.cadastro-cliente',
    xtype: 'window-cadastro-estoque',
    requires: [
        'Ext.form.FieldSet',
        'Ext.form.FieldContainer',
        'Ext.form.field.Text',
        'Ext.form.field.ComboBox',
        'Ext.container.Container'
    ],

    controller: {

        fecharJanela: function () {
            var me = this,
                vw = me.getView();

            if (vw && !vw.destroyed && !vw.isDestroying) {
                vw.close();
            }
        },

        onValidaFormulario: function (form, valid) {
            this.lookup('btnSalvar').setDisabled(!valid);
        },

        salvarPeca: function () {
            var me = this,
                vw = me.getView(),
                form = vw.down('form').getForm().getValues();

            var dados = {
                nome: form.nome,
                quantidade: form.quantidade,
                preco: form.preco
            };

            console.log('Enviando:', dados);

            Ext.Ajax.request({
                url: sistemaOsLocal.apiUrl + '/peca/adicionar',
                method: 'POST',

                jsonData: dados,

                success: function (conn) {
                    var r = Ext.JSON.decode(conn.responseText, true);

                    if (r) {
                        // Avisa quem abriu a janela que o cadastro terminou
                        vw.fireEvent('pecasalva');

                        // Fecha a janela
                        vw.close();
                    } else {
                        Ext.Msg.alert(
                            'Erro',
                            'Não foi possível cadastrar a peça.'
                        );
                    }
                },

                failure: function (conn) {
                    console.error('Erro:', conn.responseText);

                    Avisos.mostrarServidorIndisponivel();
                }
            });
        }
    },

    title: 'Cadastro Peças',
    layout: 'fit',
    resizable: false,
    width: 650,
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

    items: [{
        xtype: 'form',
        layout: 'anchor',
        reference: 'formCliente',
        listeners: {
            validitychange: 'onValidaFormulario'
        },
        defaults: {
            anchor: '100%',
            labelWidth: 100,
            labelAlign: 'right',
            margin: '5 0'
        },
        items: [{
            xtype: 'textfield',
            fieldLabel: 'Nome',
            name: 'nome',
            allowBlank: false,
            blankText : 'O campo é obrigatório',
            emptyText: 'Digite o nome completo'
        }, {
            xtype: 'container',
            defaultType: 'textfield',
            layout: 'anchor',
            defaults: {
                anchor: '100%',
                componentCls: ""
            },
            items: [{
                xtype: 'fieldcontainer',
                layout: 'hbox',
                defaults: {
                    labelAlign: 'right',
                },
                items: [{
                    xtype: 'numberfield',
                    fieldLabel: 'Quantidade',
                    allowBlank: false,
                    blankText : 'O campo é obrigatório',
                    name: 'quantidade',
                    flex: 1,
                    emptyText: 'Quantidade',
                }, {
                    xtype: 'numberfield',
                    fieldLabel: 'Preço',
                    allowBlank: false,
                    blankText : 'O campo é obrigatório',
                    name: 'preco',
                    flex: 1,
                    emptyText: 'R$',
                }]
            }]
        }]
    }],
    buttons: [{
        text: 'Cancelar',
        iconCls: 'fa fa-times',
        handler: 'fecharJanela'
    }, {
        text: 'Salvar',
        reference: 'btnSalvar',
        disabled: true,
        handler: 'salvarPeca',
        iconCls: 'fa fa-check'
    }]
});