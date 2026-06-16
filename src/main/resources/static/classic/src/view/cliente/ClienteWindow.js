Ext.define('ProjSistemaOs.view.cliente.ClienteWindow', {
    extend: 'Ext.panel.Panel',
    // alias: 'widget.cadastro-cliente',
	xtype: 'window-cadastro-cliente',
	controller: 'cliente-controller',
	requires: [
		'ProjSistemaOs.view.cliente.ClienteController',
		'Ext.form.FieldSet',
		'Ext.form.FieldContainer',
		'Ext.form.field.Text',
		'Ext.form.field.ComboBox',
		'Ext.container.Container'
	],

    title: 'Cadastro Cliente',
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
				maskRe: /[0-9]/,
				maxLengthText: 'O máximo de caracteres é de {0}',
				listeners: {
					change: function(field, value) {
						value = value.replace(/\D/g, '');

						if (value.length > 11) {
							value = value.substring(0, 11);
						}

						if (value.length > 10) {
							value = value.replace(
								/^(\d{2})(\d{5})(\d{4}).*/,
								'($1) $2-$3'
							);
						} else {
							value = value.replace(
								/^(\d{2})(\d{4})(\d{0,4}).*/,
								'($1) $2-$3'
							);
						}

						field.suspendEvents();
						field.setValue(value);
						field.resumeEvents();
					}
				}
            }]
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
				defaultType: 'textfield',
				defaults: {
					labelAlign: 'right',
				},
				items: [{
	                fieldLabel: 'Rua',
	                name: 'rua',
	                flex: 2,
	                emptyText: 'First',
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
				    emptyText: 'First',
				}]
			}, {
				xtype: 'textareafield',
				fieldLabel: 'Complemento',
				name: 'complemento',
				height: 100,
				emptyText: 'Informações adicionais'
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
		handler: 'salvarCliente'
	}]
});