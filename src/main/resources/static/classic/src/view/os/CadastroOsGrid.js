Ext.define('ProjSistemaOs.view.os.CadastroOsGrid', {
    extend: 'Ext.grid.Panel',
    xtype: 'cadastro-os-grid',

    title: 'Clientes',
    layout: 'fit',
    tbar: [{
        xtype: 'button',
        tooltip: 'Adicionar',
        iconCls: 'fa fa-plus',
        handler: 'adicionarCliente'
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
        text: 'Id'
    }, {
        text: 'Data de criação'
    }, {
        text: 'Nome cliente'
    }, {
        text: 'Status'
    }]
});