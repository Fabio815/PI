Ext.define('ProjSistemaOs.view.os.HistoricoOsGridTemplate', {
    extend: 'Ext.grid.Panel',
    xtype: 'grid-template-historico',

    config: {
        osId: null
    },

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
            a.headerCt.on('menucreate', function (ct, menu) {
                menu.on('beforeshow', function () {
                    var coluna = ct.getMenu().activeHeader,
                        ordenavel = coluna && coluna.sortable;

                    menu.down('#ascItem').setHidden(!ordenavel);
                    menu.down('#descItem').setHidden(!ordenavel);
                });
            });
        },
        limparPesquisa: function (a, b, e) {
            if (a = a.up('grid')) a.filters.clearFilters(),
                a.getStore().getSorters().removeAll()
        },
        onEsc: function () {
            this.getView().destroy()
        }
    },
    initComponent: function () {
        this.callParent(arguments);

        var osId = this.getOsId();
        var store = this.getViewModel().getStore('historicos');

        if (!osId) {
            Ext.raise('HistoricoOsGridTemplate: osId não informado ao criar o componente.');
        }

        store.getProxy().setUrl(window.location.origin + '/os/' + osId + '/historico');
        store.load();
    },

    bind: {
        store: '{historicos}'
    },

    viewModel: {
        stores: {
            historicos: {
                fields: [{
                    name: 'dataAlteracao',
                    type: 'date',
                    dateFormat: 'Y-m-d\\TH:i:s'
                }, {
                    name: 'campoAlterado',
                    type: 'string'
                }, {
                    name: 'id',
                    type: 'int'
                }, {
                    name: 'de',
                    type: 'string'
                }, {
                    name: 'para',
                    type: 'string'
                }, {
                    name: 'usuario'
                }, {
                    name: 'usuarioNome',
                    type: 'string',
                    persist: false,
                    depends: ['usuario'],
                    calculate: function (data) {
                        return data.usuario ? data.usuario.nome : '';
                    }
                }],
                proxy: {
                    type: 'ajax',
                    url: window.location.origin + '/os/historico',
                    method: 'GET',
                    reader: {
                        type: 'json'
                    }
                },
                pageSize: 0,
                remoteFilter: false,
                remoteSort: false,
                autoLoad: false,
                autoDestroy: true,
                sorters: [
                    {
                        property: 'dataAlteracao',
                        direction: 'DESC'
                    }
                ]
            }
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
    defaultAlign: 'c-c',
    border: false,
    columnLines: false,
    scrollable: 'y',
    enableColumnHide: false,
    enableColumnMove: false,
    enableColumnResize: false,
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
            text: 'Usuário',
            tpl: [
                '<tpl for="usuario">',
                '{nome:htmlEncode}',
                '</tpl>'
            ],
            flex: 2,
            requiresMenu: true,
            filter: {
                type: 'string',
                dataIndex: 'usuarioNome'
            }
        }, {
            xtype: 'gridcolumn',
            dataIndex: 'dataAlteracao',
            sortable: true,
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
            dataIndex: 'id',
            text: 'Id',
            renderer: Ext.util.Format.htmlEncode,
            flex: 1,
            requiresMenu: false
        }, {
            xtype: 'gridcolumn',
            dataIndex: 'campoAlterado',
            text: 'Descrição',
            renderer: Ext.util.Format.htmlEncode,
            flex: 3,
            requiresMenu: true,
            filter: 'string'
        }, {
            dataIndex: 'de',
            text: 'De',
            tpl: [
                '<div style="white-space: pre-wrap">{de:htmlEncode}</div>'
            ],
            flex: 4,
            requiresMenu: true,
            //filter: 'string'
        }, {
            dataIndex: 'para',
            text: 'Para',
            tpl: [
                '<div style="white-space: pre-wrap">{para:htmlEncode}</div>'
            ],
            flex: 4,
            requiresMenu: true,
            //filter: 'string'
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
                xtype: 'tbtext',
                bind: {
                    text: '{historicos.count} registro(s)'
                }
            },
            '-',
            {
                xtype: 'button',
                iconCls: 'fas fa-ban',
                tooltip: 'Limpar filtros',
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