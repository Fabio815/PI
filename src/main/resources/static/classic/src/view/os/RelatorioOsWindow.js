Ext.define('ProjSistemaOs.view.os.RelatorioOsWindow', {
    extend: 'Ext.window.Window',
    xtype: 'relatorio-os-window',

    requires: [
        'Ext.grid.Panel',
        'Ext.form.field.Date',
        'Ext.form.field.ComboBox'
    ],

    controller: {
        init: function () {
            var me = this;
            me.start = 0;
            me.limit = 25;
            me.dadosStatus = [];
            me.dadosPeriodo = [];
            me.filtrosAplicados = {};
            me.filtrosEmExibicao = {};
            me.carregarResponsaveis();
            me.carregarRelatorio();
        },

        carregarResponsaveis: function () {
            var me = this;
            Ext.Ajax.request({
                url: sistemaOsLocal.apiUrl + '/os/responsaveis',
                params: { incluirInativos: true },
                method: 'GET',
                success: function (response) {
                    var view = me.getView(), combo, dados;
                    if (!view || view.destroyed) {
                        return;
                    }
                    dados = Ext.JSON.decode(response.responseText, true);
                    combo = me.lookupReference('responsavelFiltro');
                    if (Ext.isArray(dados) && combo && !combo.destroyed) {
                        combo.getStore().loadData(dados);
                    }
                },
                failure: function () {
                    var view = me.getView();
                    if (view && !view.destroyed) {
                        me.mostrarErro('Não foi possível carregar a lista de responsáveis.');
                    }
                }
            });
        },

        aplicarFiltros: function () {
            var me = this,
                inicio = me.lookupReference('dataInicioFiltro').getValue(),
                fim = me.lookupReference('dataFimFiltro').getValue();

            if (!me.lookupReference('filtrosForm').getForm().isValid()) {
                Ext.Msg.alert('Atenção', 'Corrija os filtros de data antes de pesquisar.');
                return;
            }
            if (inicio && fim && Ext.Date.format(inicio, 'Y-m-d') > Ext.Date.format(fim, 'Y-m-d')) {
                Ext.Msg.alert('Atenção', 'A data inicial deve ser anterior ou igual à data final.');
                return;
            }
            me.start = 0;
            me.filtrosAplicados = me.montarParametros();
            delete me.filtrosAplicados.start;
            delete me.filtrosAplicados.limit;
            me.carregarRelatorio();
        },

        limparFiltros: function () {
            this.lookupReference('filtrosForm').getForm().reset();
            this.start = 0;
            this.filtrosAplicados = {};
            this.carregarRelatorio();
        },

        paginaAnterior: function () {
            if (this.start > 0) {
                this.start = Math.max(0, this.start - this.limit);
                this.carregarRelatorio();
            }
        },

        proximaPagina: function () {
            var me = this,
                total = me.total || 0;
            if (me.start + me.limit < total) {
                me.start += me.limit;
                me.carregarRelatorio();
            }
        },

        montarParametros: function () {
            var me = this,
                params = { start: me.start, limit: me.limit },
                inicio = me.lookupReference('dataInicioFiltro').getValue(),
                fim = me.lookupReference('dataFimFiltro').getValue(),
                clienteId = me.lookupReference('clienteFiltro').getValue(),
                responsavelId = me.lookupReference('responsavelFiltro').getValue(),
                situacao = me.lookupReference('situacaoFiltro').getValue(),
                status = me.lookupReference('statusFiltro').getValue();

            if (inicio) { params.dataInicio = Ext.Date.format(inicio, 'Y-m-d'); }
            if (fim) { params.dataFim = Ext.Date.format(fim, 'Y-m-d'); }
            if (!Ext.isEmpty(clienteId)) { params.clienteId = clienteId; }
            if (!Ext.isEmpty(responsavelId)) { params.responsavelId = responsavelId; }
            if (!Ext.isEmpty(situacao)) { params.situacao = situacao; }
            if (!Ext.isEmpty(status)) { params.status = status; }
            return params;
        },

        carregarRelatorio: function () {
            var me = this,
                view = me.getView(),
                parametros = Ext.apply({}, me.filtrosAplicados || {}),
                sequencia = me.sequenciaConsulta = (me.sequenciaConsulta || 0) + 1;
            if (!view || view.destroyed) {
                return;
            }
            parametros.start = me.start;
            parametros.limit = me.limit;
            view.setLoading('Carregando relatório...');
            Ext.Ajax.request({
                url: sistemaOsLocal.apiUrl + '/os/relatorio',
                method: 'GET',
                params: parametros,
                success: function (response) {
                    if (!view || view.destroyed || sequencia !== me.sequenciaConsulta) { return; }
                    var resposta = Ext.JSON.decode(response.responseText, true);
                    if (!resposta || !Ext.isArray(resposta.linhas)) {
                        me.mostrarErro('O servidor retornou um relatório inválido.');
                        return;
                    }
                    me.total = Number(resposta.total) || 0;
                    me.lookupReference('tabelaRelatorio').getStore().loadData(resposta.linhas);
                    me.dadosStatus = Ext.isArray(resposta.porStatus) ? resposta.porStatus : [];
                    me.dadosPeriodo = Ext.isArray(resposta.porPeriodo) ? resposta.porPeriodo : [];
                    me.filtrosEmExibicao = resposta.filtros || me.filtrosAplicados;
                    me.atualizarPaginacao(resposta.linhas.length);
                    me.redesenharGraficos();
                },
                failure: function (response) {
                    if (!view || view.destroyed || sequencia !== me.sequenciaConsulta) { return; }
                    var corpo = Ext.JSON.decode(response.responseText || '{}', true),
                        mensagem = corpo && corpo.mensagemErro ? corpo.mensagemErro : 'Não foi possível carregar o relatório.';
                    me.mostrarErro(mensagem);
                },
                callback: function () {
                    if (view && !view.destroyed && sequencia === me.sequenciaConsulta) {
                        view.setLoading(false);
                    }
                }
            });
        },

        atualizarPaginacao: function (quantidadePagina) {
            var me = this,
                total = me.total || 0,
                primeira = total ? me.start + 1 : 0,
                ultima = Math.min(me.start + quantidadePagina, total),
                paginas = Math.max(1, Math.ceil(total / me.limit)),
                paginaAtual = Math.floor(me.start / me.limit) + 1;

            me.lookupReference('rotuloPaginacao').setHtml(
                'Exibindo ' + primeira + '–' + ultima + ' de ' + total + ' OS (página ' + paginaAtual + ' de ' + paginas + ')'
            );
            me.lookupReference('btnAnterior').setDisabled(me.start <= 0);
            me.lookupReference('btnProxima').setDisabled(me.start + me.limit >= total);
        },

        mostrarErro: function (mensagem) {
            Ext.Msg.alert('Relatório', Ext.String.htmlEncode(String(mensagem || 'Ocorreu um erro.')));
        },

        textoSituacao: function (valor) {
            var rotulos = {
                PENDENTE: 'Pendente',
                EM_ANDAMENTO: 'Em andamento',
                CONCLUIDO: 'Concluído',
                CANCELADO: 'Cancelado'
            };
            return rotulos[valor] || String(valor || '—');
        },

        redesenharGraficos: function () {
            var me = this,
                status = me.lookupReference('graficoStatus'),
                periodo = me.lookupReference('graficoPeriodo');
            if (!status || !periodo || status.destroyed || periodo.destroyed || !status.rendered || !periodo.rendered) {
                return;
            }
            status.setHtml(me.criarGraficoStatus(me.dadosStatus, Math.max(status.getWidth(), 340)));
            periodo.setHtml(me.criarGraficoPeriodo(me.dadosPeriodo, Math.max(periodo.getWidth(), 340)));
        },

        criarGraficoStatus: function (dados, largura) {
            var me = this,
                nomes = { PENDENTE: 'Pendente', EM_ANDAMENTO: 'Em andamento', CONCLUIDO: 'Concluído', CANCELADO: 'Cancelado' },
                cores = { PENDENTE: '#d38c16', EM_ANDAMENTO: '#3676c8', CONCLUIDO: '#2d9b68', CANCELADO: '#bd4a4a' },
                ordem = ['PENDENTE', 'EM_ANDAMENTO', 'CONCLUIDO', 'CANCELADO'],
                mapa = {},
                maximo = 0,
                topo = 24,
                passo = 48,
                esquerda = 142,
                direita = 35,
                altura = topo + passo * ordem.length + 8,
                area = Math.max(120, largura - esquerda - direita),
                svg = '<svg xmlns="http://www.w3.org/2000/svg" role="img" aria-label="Ordens de serviço por situação" viewBox="0 0 ' + largura + ' ' + altura + '" width="100%" height="100%">',
                linhasTabela = '', i, chave, valor, barra;

            Ext.Array.each(dados || [], function (item) {
                if (item && nomes[item.situacao]) {
                    mapa[item.situacao] = Number(item.total) || 0;
                }
            });
            Ext.Array.each(ordem, function (item) { maximo = Math.max(maximo, mapa[item] || 0); });
            maximo = Math.max(maximo, 1);
            for (i = 0; i < ordem.length; i++) {
                chave = ordem[i];
                valor = mapa[chave] || 0;
                barra = Math.round(area * valor / maximo);
                svg += '<text x="' + (esquerda - 9) + '" y="' + (topo + i * passo + 21) + '" text-anchor="end" font-size="13" fill="#344054">' + nomes[chave] + '</text>';
                svg += '<rect x="' + esquerda + '" y="' + (topo + i * passo + 4) + '" width="' + barra + '" height="24" rx="4" fill="' + cores[chave] + '"><title>' + nomes[chave] + ': ' + valor + '</title></rect>';
                svg += '<text x="' + (esquerda + barra + 7) + '" y="' + (topo + i * passo + 21) + '" font-size="13" fill="#344054">' + valor + '</text>';
                linhasTabela += '<tr><th scope="row">' + nomes[chave] + '</th><td>' + valor + '</td></tr>';
            }
            if (!maximo || !Ext.Array.some(ordem, function (item) { return (mapa[item] || 0) > 0; })) {
                svg += '<text x="' + (esquerda + 8) + '" y="' + (altura - 8) + '" font-size="12" fill="#667085">Nenhuma OS encontrada para os filtros.</text>';
            }
            svg += '</svg>';
            return '<div style="width:100%;height:100%;overflow:auto">' + svg +
                '<details><summary>Ver dados do gráfico de situação</summary><table><caption>Ordens por situação</caption><thead><tr><th>Situação</th><th>Total</th></tr></thead><tbody>' + linhasTabela + '</tbody></table></details></div>';
        },

        criarGraficoPeriodo: function (dados, largura) {
            var me = this,
                totais = {},
                dias = [],
                datas = Ext.Array.map(dados || [], function (item) { return item && /^\d{4}-\d{2}-\d{2}$/.test(item.periodo) ? item.periodo : null; }).filter(function (item) { return !!item; }).sort(),
                filtros = me.filtrosEmExibicao || {},
                inicio = filtros.dataInicio || (datas.length ? datas[0] : null),
                fim = filtros.dataFim || (datas.length ? datas[datas.length - 1] : null),
                totalDias = 0,
                cursor,
                fimDate,
                etiqueta,
                maximo = 0,
                svg,
                topo = 22,
                baixo = 46,
                esquerda = 48,
                direita = 18,
                altura = 225,
                areaLargura = Math.max(180, largura - esquerda - direita),
                areaAltura = altura - topo - baixo,
                pontos = [],
                labels = '',
                tabela = '',
                i,
                x,
                y,
                valor,
                step,
                ticks,
                path = '';

            Ext.Array.each(dados || [], function (item) {
                if (item && /^\d{4}-\d{2}-\d{2}$/.test(item.periodo)) {
                    totais[item.periodo] = Number(item.total) || 0;
                }
            });
            if (inicio && fim) {
                cursor = me.dataIsoParaLocal(inicio);
                fimDate = me.dataIsoParaLocal(fim);
                if (cursor && fimDate && cursor <= fimDate) {
                    totalDias = Math.round((fimDate.getTime() - cursor.getTime()) / 86400000) + 1;
                    if (totalDias <= 3660) {
                        while (cursor <= fimDate) {
                            dias.push({ periodo: me.dataLocalParaIso(cursor), total: totais[me.dataLocalParaIso(cursor)] || 0 });
                            cursor.setDate(cursor.getDate() + 1);
                        }
                    }
                }
            }
            if (!dias.length) {
                Ext.Array.each(datas, function (data) {
                    dias.push({ periodo: data, total: totais[data] || 0 });
                });
            }
            if (!dias.length) {
                return '<div role="status" aria-live="polite" style="padding:18px;color:#667085">Nenhuma OS encontrada para exibir na série diária.</div><details><summary>Dados do gráfico por período</summary><p>Nenhum dado disponível.</p></details>';
            }

            Ext.Array.each(dias, function (item) { maximo = Math.max(maximo, item.total); });
            maximo = Math.max(maximo, 1);
            Ext.Array.each(dias, function (item, index) {
                x = dias.length === 1 ? esquerda + areaLargura / 2 : esquerda + index * areaLargura / (dias.length - 1);
                y = topo + areaAltura - (item.total / maximo) * areaAltura;
                pontos.push({ x: x, y: y, periodo: item.periodo, total: item.total });
            });
            svg = '<svg xmlns="http://www.w3.org/2000/svg" role="img" aria-label="Ordens de serviço emitidas por dia" viewBox="0 0 ' + largura + ' ' + altura + '" width="100%" height="100%">';
            ticks = Math.min(4, maximo);
            for (i = 0; i <= ticks; i++) {
                y = topo + areaAltura * i / ticks;
                valor = Math.round(maximo * (ticks - i) / ticks);
                svg += '<line x1="' + esquerda + '" y1="' + y + '" x2="' + (largura - direita) + '" y2="' + y + '" stroke="#e4e7ec" stroke-width="1" />';
                svg += '<text x="' + (esquerda - 7) + '" y="' + (y + 4) + '" text-anchor="end" font-size="10" fill="#667085">' + valor + '</text>';
            }
            Ext.Array.each(pontos, function (ponto, index) {
                path += (index ? ' L ' : 'M ') + ponto.x.toFixed(1) + ' ' + ponto.y.toFixed(1);
            });
            svg += '<path d="' + path + '" fill="none" stroke="#3676c8" stroke-width="2.5" stroke-linejoin="round" stroke-linecap="round" />';
            step = Math.max(1, Math.ceil(pontos.length / 7));
            for (i = 0; i < pontos.length; i += step) {
                etiqueta = pontos[i].periodo.substring(8, 10) + '/' + pontos[i].periodo.substring(5, 7);
                svg += '<circle cx="' + pontos[i].x.toFixed(1) + '" cy="' + pontos[i].y.toFixed(1) + '" r="3.5" fill="#3676c8"><title>' + pontos[i].periodo + ': ' + pontos[i].total + '</title></circle>';
                svg += '<text x="' + pontos[i].x.toFixed(1) + '" y="' + (altura - 24) + '" text-anchor="middle" font-size="10" fill="#667085">' + etiqueta + '</text>';
            }
            if ((pontos.length - 1) % step !== 0 && pontos.length > 1) {
                i = pontos.length - 1;
                etiqueta = pontos[i].periodo.substring(8, 10) + '/' + pontos[i].periodo.substring(5, 7);
                svg += '<circle cx="' + pontos[i].x.toFixed(1) + '" cy="' + pontos[i].y.toFixed(1) + '" r="3.5" fill="#3676c8"><title>' + pontos[i].periodo + ': ' + pontos[i].total + '</title></circle>';
                svg += '<text x="' + pontos[i].x.toFixed(1) + '" y="' + (altura - 24) + '" text-anchor="middle" font-size="10" fill="#667085">' + etiqueta + '</text>';
            }
            svg += '<text x="' + esquerda + '" y="' + (altura - 5) + '" font-size="10" fill="#667085">Emissão diária</text></svg>';
            for (i = 0; i < dias.length && i < 366; i++) {
                tabela += '<tr><th scope="row">' + dias[i].periodo + '</th><td>' + dias[i].total + '</td></tr>';
            }
            if (dias.length > 366) {
                tabela += '<tr><td colspan="2">Tabela limitada aos primeiros 366 dias; a série do gráfico contém ' + dias.length + ' dias.</td></tr>';
            }
            return '<div style="width:100%;height:100%;overflow:auto">' + svg +
                '<details><summary>Ver dados do gráfico por período</summary><table><caption>OS por data de emissão</caption><thead><tr><th>Data</th><th>Total</th></tr></thead><tbody>' + tabela + '</tbody></table></details></div>';
        },

        dataIsoParaLocal: function (valor) {
            var partes = String(valor || '').split('-');
            return partes.length === 3 ? new Date(Number(partes[0]), Number(partes[1]) - 1, Number(partes[2])) : null;
        },

        dataLocalParaIso: function (data) {
            var ano = data.getFullYear(),
                mes = String(data.getMonth() + 1),
                dia = String(data.getDate());
            return ano + '-' + (mes.length < 2 ? '0' + mes : mes) + '-' + (dia.length < 2 ? '0' + dia : dia);
        }
    },

    title: 'Relatório de ordens de serviço',
    iconCls: 'fa fa-chart-bar',
    modal: true,
    resizable: true,
    maximizable: true,
    width: 1180,
    height: 900,
    minWidth: 900,
    minHeight: 700,
    layout: 'fit',
    listeners: {
        afterrender: function (window) {
            if (window.getController()) { window.getController().redesenharGraficos(); }
        },
        resize: function (window) {
            if (window.getController()) {
                window.getController().redesenharGraficos();
            }
        }
    },

    items: [{
        xtype: 'panel',
        layout: { type: 'vbox', align: 'stretch' },
        bodyPadding: 10,
        scrollable: 'y',
        items: [{
            xtype: 'form',
            reference: 'filtrosForm',
            title: 'Filtros',
            bodyPadding: '8 10',
            layout: { type: 'vbox', align: 'stretch' },
            defaults: { labelAlign: 'top', margin: '0 8 0 0' },
            items: [{
                xtype: 'container',
                layout: { type: 'hbox', align: 'end' },
                defaults: { labelAlign: 'top', margin: '0 10 0 0' },
                items: [{
                    xtype: 'datefield',
                    reference: 'dataInicioFiltro',
                    fieldLabel: 'Emissão a partir de',
                    format: 'd/m/Y',
                    submitFormat: 'Y-m-d',
                    width: 160,
                    allowBlank: true
                }, {
                    xtype: 'datefield',
                    reference: 'dataFimFiltro',
                    fieldLabel: 'Emissão até',
                    format: 'd/m/Y',
                    submitFormat: 'Y-m-d',
                    width: 150,
                    allowBlank: true
                }, {
                    xtype: 'combobox',
                    reference: 'clienteFiltro',
                    fieldLabel: 'Cliente',
                    width: 245,
                    queryMode: 'remote',
                    queryParam: 'nome',
                    minChars: 0,
                    forceSelection: true,
                    editable: true,
                    valueField: 'id',
                    displayField: 'nome',
                    emptyText: 'Todos os clientes',
                    listConfig: { itemTpl: '<div>{nome:htmlEncode}</div>' },
                    store: {
                        fields: ['id', 'nome', 'telefone'],
                        autoLoad: false,
                        pageSize: 25,
                        proxy: {
                            type: 'ajax',
                            url: sistemaOsLocal.apiUrl + '/cliente/listar/os',
                            method: 'GET',
                            reader: { type: 'json', rootProperty: 'listaClientes', totalProperty: 'total' }
                        }
                    }
                }, {
                    xtype: 'combobox',
                    reference: 'responsavelFiltro',
                    fieldLabel: 'Responsável',
                    width: 220,
                    queryMode: 'local',
                    forceSelection: true,
                    editable: false,
                    valueField: 'id',
                    displayField: 'nome',
                    emptyText: 'Todos os responsáveis',
                    listConfig: { itemTpl: '<div>{nome:htmlEncode}</div>' },
                    store: { fields: ['id', 'nome'], data: [] }
                }]
            }, {
                xtype: 'container',
                margin: '4 0 0 0',
                layout: { type: 'hbox', align: 'end' },
                defaults: { labelAlign: 'top', margin: '0 10 0 0' },
                items: [{
                    xtype: 'combobox',
                    reference: 'situacaoFiltro',
                    fieldLabel: 'Situação do serviço',
                    width: 220,
                    queryMode: 'local',
                    forceSelection: true,
                    editable: false,
                    valueField: 'value',
                    displayField: 'text',
                    emptyText: 'Todas as situações',
                    store: {
                        fields: ['value', 'text'],
                        data: [
                            { value: 'PENDENTE', text: 'Pendente' },
                            { value: 'EM_ANDAMENTO', text: 'Em andamento' },
                            { value: 'CONCLUIDO', text: 'Concluído' },
                            { value: 'CANCELADO', text: 'Cancelado' }
                        ]
                    }
                }, {
                    xtype: 'combobox',
                    reference: 'statusFiltro',
                    fieldLabel: 'Status',
                    width: 150,
                    queryMode: 'local',
                    forceSelection: true,
                    editable: false,
                    valueField: 'value',
                    displayField: 'text',
                    emptyText: 'Ativo e inativo',
                    store: {
                        fields: ['value', 'text'],
                        data: [
                            { value: 'ATIVO', text: 'Ativo' },
                            { value: 'INATIVO', text: 'Inativo' }
                        ]
                    }
                }, {
                    xtype: 'button',
                    text: 'Aplicar filtros',
                    iconCls: 'fa fa-search',
                    margin: '0 8 7 0',
                    handler: 'aplicarFiltros'
                }, {
                    xtype: 'button',
                    text: 'Limpar',
                    iconCls: 'fa fa-eraser',
                    margin: '0 0 7 0',
                    handler: 'limparFiltros'
                }]
            }]
        }, {
            xtype: 'grid',
            reference: 'tabelaRelatorio',
            title: 'Ordens de serviço',
            margin: '10 0 0 0',
            flex: 1,
            minHeight: 280,
            columnLines: true,
            store: {
                fields: ['id', 'dataEmissao', 'dataFim', 'clienteId', 'clienteNome', 'responsavelId', 'responsavelNome', 'situacao', 'status', 'valorTotal'],
                data: []
            },
            columns: [{
                text: 'OS', dataIndex: 'id', width: 70, align: 'right'
            }, {
                text: 'Emissão', dataIndex: 'dataEmissao', width: 105,
                renderer: function (valor) {
                    var data = valor ? Ext.Date.parse(valor, 'Y-m-d') : null;
                    return data ? Ext.Date.format(data, 'd/m/Y') : '—';
                }
            }, {
                text: 'Finalização', dataIndex: 'dataFim', width: 110,
                renderer: function (valor) {
                    var data = valor ? Ext.Date.parse(valor, 'Y-m-d') : null;
                    return data ? Ext.Date.format(data, 'd/m/Y') : '—';
                }
            }, {
                text: 'Cliente', dataIndex: 'clienteNome', flex: 2, minWidth: 150,
                renderer: function (valor) { return Ext.String.htmlEncode(String(valor || '—')); }
            }, {
                text: 'Responsável', dataIndex: 'responsavelNome', flex: 2, minWidth: 140,
                renderer: function (valor) { return Ext.String.htmlEncode(String(valor || '—')); }
            }, {
                text: 'Situação', dataIndex: 'situacao', width: 130,
                renderer: function (valor) {
                    var nomes = { PENDENTE: 'Pendente', EM_ANDAMENTO: 'Em andamento', CONCLUIDO: 'Concluído', CANCELADO: 'Cancelado' };
                    return Ext.String.htmlEncode(nomes[valor] || String(valor || '—'));
                }
            }, {
                text: 'Status', dataIndex: 'status', width: 85,
                renderer: function (valor) {
                    return valor === 'ATIVO' ? 'Ativo' : (valor === 'INATIVO' ? 'Inativo' : '—');
                }
            }, {
                text: 'Valor total', dataIndex: 'valorTotal', width: 135, align: 'right',
                renderer: function (valor, meta) {
                    meta.style = 'text-align:right;font-variant-numeric:tabular-nums;';
                    var numero = Number(valor);
                    return new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' }).format(isFinite(numero) ? numero : 0);
                }
            }]
        }, {
            xtype: 'container',
            margin: '4 0 10 0',
            layout: { type: 'hbox', align: 'middle', pack: 'center' },
            items: [{
                xtype: 'button', reference: 'btnAnterior', text: 'Anterior', iconCls: 'fa fa-chevron-left', disabled: true, handler: 'paginaAnterior'
            }, {
                xtype: 'component', reference: 'rotuloPaginacao', margin: '0 14',
                html: 'Exibindo 0–0 de 0 OS (página 1 de 1)',
                style: { color: '#344054', fontWeight: '600' }
            }, {
                xtype: 'button', reference: 'btnProxima', text: 'Próxima', iconCls: 'fa fa-chevron-right', disabled: true, handler: 'proximaPagina'
            }]
        }, {
            xtype: 'container',
            layout: { type: 'hbox', align: 'stretch' },
            height: 330,
            defaults: { flex: 1, layout: 'fit', bodyPadding: 8, margin: '0 5 0 0' },
            items: [{
                xtype: 'panel',
                title: 'Ordens por situação',
                items: [{ xtype: 'component', reference: 'graficoStatus', autoEl: { tag: 'div' } }]
            }, {
                xtype: 'panel',
                title: 'Emissões por dia',
                margin: 0,
                items: [{ xtype: 'component', reference: 'graficoPeriodo', autoEl: { tag: 'div' } }]
            }]
        }]
    }],

    buttons: [{
        text: 'Fechar',
        iconCls: 'fa fa-times',
        handler: function (button) { button.up('relatorio-os-window').close(); }
    }]
});
