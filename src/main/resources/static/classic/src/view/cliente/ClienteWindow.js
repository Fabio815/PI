Ext.define('ProjSistemaOs.view.cliente.ClienteWindow', {
    extend: 'Ext.window.Window',
    alias: 'widget.cadastro-cliente',
	controller: 'cliente-controller',
	requires: [
		'ProjSistemaOs.view.cliente.ClienteController'
	],

    title: 'Cadastro Cliente',
    layout: 'fit',
    resizable: false,
    modal: true,
    width: 650,
    bodyPadding: 15,

    items: [{
        xtype: 'form',
        layout: 'anchor',
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
            layout: 'hbox',
            defaults: {
                labelAlign: 'right'
            },
            items: [{
                xtype: 'textfield',
                fieldLabel: 'Telefone',
				allowBlank: false,
				blankText : 'O campo é obrigatório',
                name: 'telefone',
                emptyText: '(00) 00000-0000',
				//enforceMaxLength: true,
				maxLength: 15,
				maxLengthText: 'O máximo de caracteres é de {0}'
            }]
        }, {
			xtype: 'fieldset',
			title: 'Endereço',
			defaultType: 'textfield',
			layout: 'anchor',
			defaults: {
				anchor: '100%',
				componentCls: ""
			},
			items: [{
				xtype: 'fieldcontainer',
				layout: 'hbox',
				defaultType: 'textfield',
				defaults: {
					labelAlign: 'right',
				},
				items: [{
	                fieldLabel: 'Rua',
	                name: 'rua',
	                flex: 2,
	                emptyText: 'rua',
	            }, {
					xtype: 'numberfield',
	                fieldLabel: 'Número',
	                name: 'numero',
	                flex: 1,
	                emptyText: 'Ex 1221',
	            }]
			}, {
				xtype: 'fieldcontainer',
				layout: 'hbox',
				defaultType: 'textfield',
				defaults: {
					labelAlign: 'right',
				},
				items: [{
				    fieldLabel: 'Logradouro',
				    name: 'logradouro',
				    flex: 1,
				    emptyText: 'logradouro',
				}]
			}, {
				xtype: 'textareafield',
				fieldLabel: 'Complemento',
				name: 'complemento',
				height: 100,
				emptyText: 'Informações adicionais'
			}]
        }],
        
        buttons: [{
            text: 'Cancelar',
			iconCls: 'fa fa-times',
			handler: 'fecharJanela'
        }, {
            text: 'Cadastrar',
			iconCls: 'fa fa-check',
			handler: 'salvarCliente',
			disabled: true,
			formBind: true
        }]
    }]
});