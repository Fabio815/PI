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
        text: 'Status',
        flex: 3
    }, {
        text: 'Data de finalização',
        flex: 2
    }, {
        text: 'Ativo',
        flex: 1
    }]
});