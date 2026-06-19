Ext.define('ProjSistemaOs.view.os.CadastroOsGrid', {
    extend: 'Ext.grid.Panel',
    xtype: 'cadastro-os-grid',

    requires: [
        'ProjSistemaOs.view.os.CadastroOsWindow',
        'ProjSistemaOs.store.Os'
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

    store: {
        type: 'os-listagem-store'
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
        dataIndex: 'id',
        flex: 1
    }, {
        text: 'Data de criação',
        dataIndex: 'dataInicio',
        flex: 2,
        renderer: function (value) {
            return value ? Ext.Date.format(value, 'd/m/Y') : '';
        }
    }, {
        text: 'Nome cliente',
        dataIndex: 'nomeCliente',
        flex: 4
    }, {
        text: 'Telefone',
        dataIndex: 'telefone',
        flex: 2
    }, {
        text: 'Preço',
        dataIndex: 'preco',
        flex: 1
    }, {
        text: 'Situação',
        dataIndex: 'situacao',
        flex: 3,
        renderer: function (value, metaData) {
            let cor = '';
            switch (value) {
                case 'Concluído':
                    cor = 'background-color: #46C248; color: white;';
                    break;
                case 'Em Andamento':
                    cor = 'background-color: #3D69D1; color: white;';
                    break;
                case 'Cancelado':
                    cor = 'background-color: #C74040; color: white;';
                    break;
                default:
                    cor = '';
                    break;
            }
            metaData.style = cor;
            return value;
        }
    }, {
        text: 'Data de finalização',
        dataIndex: 'dataFim',
        flex: 2,
        renderer: function (value) {
            return value ? Ext.Date.format(value, 'd/m/Y') : '';
        }
    }, {
        text: 'Ativo',
        dataIndex: 'status',
        flex: 1
    }]
});