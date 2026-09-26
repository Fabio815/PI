Ext.define('ProjSistemaOs.view.os.HistoricoOsGridTemplate', {
    extend: 'Ext.grid.Panel',
    xtype: 'grid-template-historico',
    controller: {
        boxReady: function (a) {
            a.mon(
                Ext.GlobalEvents,
                'resize',
                function (b, e, f) {
                    !this ||
                    this.destroyed ||
                    this.isDestroying ||
                    (
                        this.setSize(
                            Ext.getBody().getViewSize().width * 0.85,
                            Ext.getBody().getViewSize().height * 0.85
                        ),
                            this.show()
                    )
                }, a, {
                    buffer: 10
                }
            );
            a.mon(
                Ext.getBody(),
                'click',
                function (b, e) {
                    !this ||
                    this.destroyed ||
                    this.isDestroying ||
                    this.destroy()
                }, a, {
                    delegate: '.x-mask'
                }
            );
            a.on(
                'show',
                function (b, e) {
                    b.setHeight(Ext.getBody().getViewSize().height * 0.85);
                    b.setWidth(Ext.getBody().getViewSize().width * 0.85);
                    b.center()
                },
                this
            )
        },
        limparPesquisa: function (a, b, e) {
            if (a = a.up('grid')) a.filters.clearFilters(),
                a.getStore().getSorters().removeAll()
        },
        onEsc: function () {
            this.getView().destroy()
        }
    },
    keyMap: {
        ESC: 'onEsc',
        scope: 'controller'
    },
    style: {
        backgroundColor: '#ececec',
        'border-radius': '5px'
    },
    ui: 'light',
    floating: true,
    modal: true,
    defaultALign: 'c-c',
    border: false,
    columnLines: false,
    scrollable: 'y',
    enableColumnHide: false,
    enableColumnMove: false,
    enableCulumnResize: false,
    multiColumnSort: false,
    disableSelection: true,
    reserveScrollbar: true,
    plugins: {
        gridfilters: true
    },
    tbar: {
        xtype: 'toolbar',
        style: {
            backgroundColor: '#f6f6f8'
        },
        items: [{
            xtype: 'tbtext',
            text: '<span style="font-size:15px; color: black; font-weight: bold; "><i class="fas fa-history"></i> <b>Histórico de OS</b></span>',
            style: {
                'padding': '10px',
            },
        }]
    },
    columns: {
        style: {
            padding: '5px',
            border: '2px solid #cecece',
            backgroundColor: '#f6f6f8',
        },
        defaults: {
            xtype: 'templatecolumn',
            sortable: false,
            hideable: false,
            resizable: false,
            draggable: false,
            groupable: false,
            menuDisabled: false,
            cellWrap: true,
            tdCls: Ext.baseCSSPrefix + 'wrap-cell',
            style: {
                'backgroundColor': '#f6f6f8',
            }
        },
        items: [{
                dataIndex: 'usuario',
                //text: Portal.util.Util.configuraCabecalho('Usuário', 2),
                text: 'Usuário',
                tpl: [
                    '<tpl for="usuario">',
                    '{dsLogin:htmlEncode}',
                    '</tpl>'
                ],
                flex: 2,
                requiresMenu: true,
                filter: 'string'
            }, {
                xtype: 'gridcolumn',
                dataIndex: 'dtRegistro',
                sortable: true,
                //text: Portal.util.Util.configuraCabecalho('Dt. do Historico', 2),
                text: 'Dt. do Histórico',
                formatter: 'date("d/m/Y H:i:s")',
                align: 'center',
                flex: 2,
                filter: {
                    type: 'date',
                    dateFormat: 'Y-m-d'
                }
            }, {
                xtype: 'gridcolumn',
                dataIndex: 'idTemplate',
                //text: Portal.util.Util.configuraCabecalho('Id', 2),
                text: 'Id',
                renderer: Ext.util.Format.htmlEncode,
                flex: 1,
                requiresMenu: false
            }, {
                xtype: 'gridcolumn',
                dataIndex: 'descricao',
                //text: Portal.util.Util.configuraCabecalho('Descrição', 2),
                text: 'Descrição',
                renderer: Ext.util.Format.htmlEncode,
                flex: 3,
                requiresMenu: true,
                filter: 'string'
            }, {
                dataIndex: 'de',
                //text: Portal.util.Util.configuraCabecalho('De', 2),
                text: 'De',
                tpl: [
                    '<div style="white-space: pre-wrap">{de:htmlEncode}</div>'
                ],
                flex: 4,
                requiresMenu: true,
                filter: 'string'
            }, {
                dataIndex: 'para',
                //text: Portal.util.Util.configuraCabecalho('Para', 2),
                text: 'Para',
                tpl: [
                    '<div style="white-space: pre-wrap">{para:htmlEncode}</div>'
                ],
                flex: 4,
                requiresMenu: true,
                filter: 'string'
            }
        ]
    },
    bbar: {
        xtype: 'toolbar',
        style: {
            backgroundColor: '#f6f6f8',
            padding: '5px',
            border: '2px solid #cecece',
        },
        items: [
            {
                xtype: 'pagingtoolbar',
                displayInfo: true,
                displayMsg: '{0} - {1} de {2}',
                emptyMsg: 'Nenhum',
                style: {
                    backgroundColor: '#f6f6f8'
                },
            },
            '-',
            {
                xtype: 'button',
                iconCls: 'fas fa-ban',
                listeners: {
                    click: 'limparPesquisa'
                }
            },
            '->',
            {
                xtype: 'button',
                iconCls: 'fas fa-times-circle',
                text: 'Fechar',
                handler: 'onEsc'
            }
        ],
    },
});