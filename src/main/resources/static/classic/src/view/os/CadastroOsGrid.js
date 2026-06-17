Ext.define('ProjSistemaOs.view.os.CadastroOsGrid', {
    extend: 'Ext.grid.Panel',
    xtype: 'cadastro-os-grid',

    requires: [
        'ProjSistemaOs.view.os.CadastroOsWindow'
    ],

    controller: {
        adicionarOs: function () {
            var me = this, vw = me.getViewModel();

            Ext.create('ProjSistemaOs.view.os.CadastroOsWindow', {
                floating: true,
                modal: true,
                iconCls: 'fa fa-thin fa-plus',
            }).show();
        }
    },

    title: 'Clientes',
    layout: 'fit',
    tbar: [{
        xtype: 'button',
        tooltip: 'Adicionar',
        iconCls: 'fa fa-plus',
        handler: 'adicionarOs'
    }, '-', {
        xtype: 'button',
        tooltip: 'Vizualizar',
        iconCls: 'fa fa-eye',
        handler: 'recarregarGrid'
    }, '-', {
        xtype: 'button',
        tooltip: 'Recarregar',
        iconCls: 'fa fa-sync',
        handler: 'recarregarGrid'
    }, '->', {
        xtype: "button",
        iconCls: "fas fa-ban",
        tooltip: "Limpar Pesquisa",
        listeners: {
            click: "limparPesquisa"
        }
    }],
    columns: [{
        text: 'Id',
        flex: 1
    }, {
        text: 'Data de criação',
        flex: 2
    }, {
        text: 'Nome cliente',
        flex: 4
    }, {
        text: 'Telefone',
        flex: 2
    }, {
        text: 'Preço',
        flex: 1
    }, {
        text: 'Situação',
        flex: 3
    }, {
        text: 'Data de finalização',
        flex: 2
    }, {
        text: 'Ativo',
        flex: 1
    }]
});