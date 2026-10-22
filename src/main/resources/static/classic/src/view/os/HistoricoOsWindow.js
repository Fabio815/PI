Ext.define('ProjSistemaOs.view.os.HistoricoOsWindow', {
    extend: 'Ext.window.Window',
    xtype: 'historico-os-window',

    requires: ['ProjSistemaOs.util.OsOperacional'],

    osId: null,
    carregouHistoricoCompleto: false,

    controller: {
        init: function () {
            if (!this.getView().osId) {
                Ext.Msg.alert('Atenção', 'OS inválida para consultar o histórico.');
                this.getView().close();
                return;
            }
            this.carregarHistorico({}, true);
        },

        carregarHistorico: function (params, primeiraCarga) {
            var view = this.getView(), grid = view.lookupReference('gridHistorico'), query = Ext.Object.toQueryString(params || {});
            Ext.Ajax.request({
                url: OsOperacional.apiUrl('/os/' + encodeURIComponent(view.osId) + '/historico') + (query ? '?' + query : ''),
                method: 'GET',
                callback: function (options, success, response) {
                    if (!success) {
                        OsOperacional.exibirErro(response, 'Não foi possível carregar o histórico.');
                        return;
                    }
                    var eventos = OsOperacional.lista(OsOperacional.decodificar(response));
                    if (view.destroyed) {
                        return;
                    }
                    grid.getStore().loadData(eventos, false);
                    view.lookupReference('contador').setText(eventos.length + ' evento(s)');
                    if (primeiraCarga) {
                        this.carregouHistoricoCompleto = true;
                        this.carregarAtores(eventos);
                    }
                },
                scope: this
            });
        },

        carregarAtores: function (eventos) {
            var combo = this.getView().lookupReference('usuarioId'), porId = {}, atores = [];
            Ext.Array.each(eventos, function (evento) {
                var ator = evento && evento.usuario;
                if (!ator || ator.id === null || ator.id === undefined || ator.id === '') {
                    return;
                }
                var id = String(ator.id);
                if (!porId[id]) {
                    porId[id] = true;
                    atores.push({ id: ator.id, nome: ator.nome || ('Usuário #' + id) });
                }
            });
            atores.sort(function (a, b) { return String(a.nome).localeCompare(String(b.nome), 'pt-BR'); });
            combo.getStore().loadData(atores);
            combo.setDisabled(false);
        },

        filtrarHistorico: function () {
            var view = this.getView(), inicio = view.lookupReference('dataInicio').getValue(), fim = view.lookupReference('dataFim').getValue(),
                tipo = view.lookupReference('tipoEvento').getValue(), usuario = view.lookupReference('usuarioId').getValue(), params = {};
            if (!view.lookupReference('dataInicio').isValid() || !view.lookupReference('dataFim').isValid()) {
                Ext.Msg.alert('Atenção', 'Corrija os filtros de data antes de pesquisar.');
                return;
            }
            if (inicio && fim && Ext.Date.clearTime(inicio, true) > Ext.Date.clearTime(fim, true)) {
                Ext.Msg.alert('Atenção', 'A data inicial deve ser anterior ou igual à data final.');
                return;
            }
            if (!this.carregouHistoricoCompleto) {
                Ext.Msg.alert('Atenção', 'Aguarde o carregamento dos atores do histórico antes de filtrar.');
                return;
            }
            if (tipo) { params.tipoEvento = tipo; }
            if (inicio) { params.dataInicio = Ext.Date.format(inicio, 'Y-m-d'); }
            if (fim) { params.dataFim = Ext.Date.format(fim, 'Y-m-d'); }
            if (usuario !== null && usuario !== undefined && usuario !== '') { params.usuarioId = usuario; }
            this.carregarHistorico(params, false);
        },

        limparFiltros: function () {
            var view = this.getView();
            view.lookupReference('tipoEvento').setValue(null);
            view.lookupReference('dataInicio').setValue(null);
            view.lookupReference('dataFim').setValue(null);
            view.lookupReference('usuarioId').setValue(null);
            this.carregarHistorico({}, false);
        },

        formatarData: function (value) {
            if (!value) { return ''; }
            var data = Ext.isDate(value) ? value : (Ext.Date.parse(value, 'c') || new Date(value));
            return isNaN(data.getTime()) ? Ext.String.htmlEncode(String(value)) : Ext.Date.format(data, 'd/m/Y H:i:s');
        },

        nomeResponsavel: function (value) {
            return value && value.nome ? Ext.String.htmlEncode(value.nome) : '';
        }
    },

    title: 'Histórico de OS',
    modal: true,
    floating: true,
    width: 1180,
    height: 730,
    minWidth: 800,
    minHeight: 520,
    layout: 'fit',
    items: [{
        xtype: 'grid',
        reference: 'gridHistorico',
        scrollable: true,
        store: {
            fields: [
                { name: 'id', type: 'int' },
                { name: 'dataAlteracao' },
                { name: 'campoAlterado', type: 'string' },
                { name: 'de', type: 'string' },
                { name: 'para', type: 'string' },
                { name: 'usuario' },
                { name: 'tipoEvento', type: 'string' },
                { name: 'descricao', type: 'string' },
                { name: 'responsavelAnterior' },
                { name: 'novoResponsavel' }
            ],
            data: []
        },
        columns: [{
            text: 'Data',
            dataIndex: 'dataAlteracao',
            width: 150,
            renderer: function (value) { return this.up('window').getController().formatarData(value); }
        }, {
            text: 'Evento',
            dataIndex: 'tipoEvento',
            width: 165,
            renderer: function (value) { return Ext.String.htmlEncode(value || ''); }
        }, {
            text: 'Descrição',
            dataIndex: 'descricao',
            flex: 3,
            cellWrap: true,
            renderer: function (value, meta, record) {
                meta.style = 'white-space:pre-wrap;';
                return Ext.String.htmlEncode(value || record.get('campoAlterado') || '');
            }
        }, {
            text: 'Usuário',
            dataIndex: 'usuario',
            flex: 2,
            renderer: function (value) { return value && value.nome ? Ext.String.htmlEncode(value.nome) : ''; }
        }, {
            text: 'De',
            dataIndex: 'de',
            flex: 2,
            cellWrap: true,
            renderer: function (value, meta) {
                meta.style = 'white-space:pre-wrap;';
                return Ext.String.htmlEncode(value === null || value === undefined ? '' : String(value));
            }
        }, {
            text: 'Para',
            dataIndex: 'para',
            flex: 2,
            cellWrap: true,
            renderer: function (value, meta) {
                meta.style = 'white-space:pre-wrap;';
                return Ext.String.htmlEncode(value === null || value === undefined ? '' : String(value));
            }
        }, {
            text: 'Responsável anterior',
            dataIndex: 'responsavelAnterior',
            flex: 2,
            renderer: function (value) { return this.up('window').getController().nomeResponsavel(value); }
        }, {
            text: 'Novo responsável',
            dataIndex: 'novoResponsavel',
            flex: 2,
            renderer: function (value) { return this.up('window').getController().nomeResponsavel(value); }
        }]
    }],
    tbar: [{
        xtype: 'combobox',
        reference: 'tipoEvento',
        fieldLabel: 'Evento',
        labelWidth: 48,
        width: 195,
        queryMode: 'local',
        editable: false,
        clearable: true,
        valueField: 'value',
        displayField: 'text',
        emptyText: 'Todos',
        store: {
            fields: ['value', 'text'],
            data: [
                { value: 'CRIACAO', text: 'Criação' },
                { value: 'ALTERACAO_STATUS', text: 'Alteração de situação' },
                { value: 'ATRIBUICAO', text: 'Atribuição' },
                { value: 'REATRIBUICAO', text: 'Reatribuição' },
                { value: 'EDICAO_CAMPOS', text: 'Edição de campos' },
                { value: 'CANCELAMENTO', text: 'Cancelamento' },
                { value: 'CONCLUSAO', text: 'Conclusão' },
                { value: 'CRIACAO_COMENTARIO', text: 'Criação de comentário' },
                { value: 'EDICAO_COMENTARIO', text: 'Edição de comentário' },
                { value: 'REABERTURA', text: 'Reabertura' }
            ]
        }
    }, {
        xtype: 'datefield',
        reference: 'dataInicio',
        fieldLabel: 'De',
        labelWidth: 22,
        width: 155,
        format: 'd/m/Y',
        submitFormat: 'Y-m-d',
        editable: false
    }, {
        xtype: 'datefield',
        reference: 'dataFim',
        fieldLabel: 'Até',
        labelWidth: 28,
        width: 165,
        format: 'd/m/Y',
        submitFormat: 'Y-m-d',
        editable: false
    }, {
        xtype: 'combobox',
        reference: 'usuarioId',
        fieldLabel: 'Ator',
        labelWidth: 32,
        width: 230,
        queryMode: 'local',
        editable: false,
        forceSelection: true,
        valueField: 'id',
        displayField: 'nome',
        emptyText: 'Todos os usuários',
        disabled: true,
        store: { fields: ['id', 'nome'], data: [] },
        listConfig: { itemTpl: '{nome:htmlEncode}' }
    }, {
        xtype: 'button',
        text: 'Filtrar',
        iconCls: 'fa fa-search',
        handler: 'filtrarHistorico'
    }, {
        xtype: 'button',
        text: 'Limpar',
        iconCls: 'fa fa-eraser',
        handler: 'limparFiltros'
    }, '->', {
        xtype: 'tbtext',
        reference: 'contador',
        text: '0 evento(s)'
    }],
    buttons: [{
        text: 'Fechar',
        iconCls: 'fa fa-times',
        handler: function (button) { button.up('window').close(); }
    }]
});
