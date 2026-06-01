/**
 * This class is the main view for the application. It is specified in app.js as the
 * "mainView" property. That setting automatically applies the "viewport"
 * plugin causing this view to become the body element (i.e., the viewport).
 *
 * TODO - Replace this content of this view to suite the needs of your application.
 */
Ext.define('ProjSistemaOs.view.main.Main', {
    extend: 'Ext.tab.Panel',
    xtype: 'app-main',

    requires: [
        'Ext.plugin.Viewport',
        'Ext.window.MessageBox',

        'ProjSistemaOs.view.main.MainController',
        'ProjSistemaOs.view.main.MainModel',
		'ProjSistemaOs.view.cliente.ClientesGrid',
        'ProjSistemaOs.view.estoque.EstoqueGrid'
    ],

    controller: 'main',
    viewModel: 'main',

    ui: 'navigation',

    tabBarHeaderPosition: 1,
    titleRotation: 0,
    tabRotation: 0,

    header: {
        layout: {
            align: 'stretchmax'
        },
        title: {
            text: 'Sistema de OS',
            flex: 0
        },
        iconCls: 'fa-thin fa-bicycle'
    },

    tabBar: {
        flex: 1,
        layout: {
            align: 'stretch',
            overflowHandler: 'none'
        }
    },

    responsiveConfig: {
        tall: {
            headerPosition: 'top'
        },
        wide: {
            headerPosition: 'left'
        }
    },

    defaults: {
        bodyPadding: 20,
        tabConfig: {
            responsiveConfig: {
                wide: {
                    iconAlign: 'left',
                    textAlign: 'left'
                },
                tall: {
                    iconAlign: 'top',
                    textAlign: 'center',
                    width: 120
                }
            }
        }
    },

    items: [{
        title: 'Sistema de Os',
        iconCls: 'fa-home',
        // The following grid shares a store with the classic version's grid as well!
        items: [{
            xtype: 'panel',
			layout: {
				type: 'vbox',
				pack: 'center',
				align: 'center'
			},
			items: [{
				xtype: 'component',
				html: '<h1 style="font-size:48px;">Bem-vindo ao Sistema</h1>'
			}]
        }]
    }, 	{
	    title: 'Cadastro de clientes',
	    iconCls: 'x-fa fa-user',
	    layout: 'fit',
	    items: [{
	        xtype: 'clienteGrid'
	    }]
	}, {
        title: 'Cadastro de Os',
        iconCls: 'fa-clipboard-list',
    }, {
        title: 'Estoque',
        iconCls: 'fa-box',
        items: [{
            xtype: 'estoqueGrid'
        }]
    },	{
        title: 'Usuários',
        iconCls: 'fa-cog',
        items: [{
                  xtype: 'usuario-grid'
        }]
    }]
});