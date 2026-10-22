Ext.define('ProjSistemaOs.view.os.ComentariosOsWindow', {
    extend: 'Ext.window.Window',
    xtype: 'comentarios-os-window',

    requires: [
        'ProjSistemaOs.util.Sessao',
        'ProjSistemaOs.util.OsOperacional',
        'Ext.grid.column.Action'
    ],

    osId: null,

    controller: {
        init: function () {
            var view = this.getView();
            if (view.osId) {
                this.carregarComentarios();
            }
        },

        carregarComentarios: function () {
            var view = this.getView(), grid = view.lookupReference('gridComentarios');
            Ext.Ajax.request({
                url: OsOperacional.apiUrl('/os/' + encodeURIComponent(view.osId) + '/comentarios'),
                method: 'GET',
                callback: function (options, success, response) {
                    if (!success) {
                        OsOperacional.exibirErro(response, 'Não foi possível carregar os comentários.');
                        return;
                    }
                    var comentarios = OsOperacional.lista(OsOperacional.decodificar(response));
                    if (!view.destroyed) {
                        grid.getStore().loadData(comentarios, false);
                    }
                }
            });
        },

        criarComentario: function () {
            var view = this.getView(), campo = view.lookupReference('novoComentario'), conteudo = String(campo.getValue() || '').trim();
            if (!conteudo) {
                Ext.Msg.alert('Atenção', 'O comentário não pode ficar vazio.');
                return;
            }
            if (conteudo.length > 4000) {
                Ext.Msg.alert('Atenção', 'O comentário deve ter no máximo 4000 caracteres.');
                return;
            }
            campo.setDisabled(true);
            Ext.Ajax.request({
                url: OsOperacional.apiUrl('/os/' + encodeURIComponent(view.osId) + '/comentarios'),
                method: 'POST',
                jsonData: { conteudo: conteudo },
                callback: function (options, success, response) {
                    if (view.destroyed) {
                        return;
                    }
                    campo.setDisabled(false);
                    if (success) {
                        campo.setValue('');
                        this.carregarComentarios();
                        view.fireEvent('comentariosalterados');
                    } else {
                        OsOperacional.exibirErro(response, 'Não foi possível publicar o comentário.');
                    }
                },
                scope: this
            });
        },

        editarComentario: function (grid, rowIndex) {
            var controller = this, view = this.getView(), record = grid.getStore().getAt(rowIndex), autor = record && record.get('autor'),
                podeEditar = Sessao.isAdm() || (autor && String(autor.id) === String(Sessao.usuarioId));
            if (!record || !podeEditar) {
                Ext.Msg.alert('Atenção', 'Somente o autor do comentário ou um administrador pode editá-lo.');
                return;
            }
            var form = Ext.create('Ext.form.Panel', {
                bodyPadding: 12,
                defaults: { anchor: '100%' },
                items: [{
                    xtype: 'textarea',
                    name: 'conteudo',
                    fieldLabel: 'Comentário',
                    labelAlign: 'top',
                    allowBlank: false,
                    maxLength: 4000,
                    enforceMaxLength: true,
                    value: record.get('conteudo') || '',
                    height: 150
                }]
            });
            var editor = Ext.create('Ext.window.Window', {
                title: 'Editar comentário',
                modal: true,
                width: 500,
                layout: 'fit',
                items: [form],
                buttons: [{ text: 'Cancelar', handler: function () { editor.close(); } }, {
                    text: 'Salvar',
                    iconCls: 'fa fa-check',
                    handler: function () {
                        var value = String(form.getValues().conteudo || '').trim();
                        if (!value || value.length > 4000 || !form.isValid()) {
                            Ext.Msg.alert('Atenção', value ? 'O comentário deve ter no máximo 4000 caracteres.' : 'O comentário não pode ficar vazio.');
                            return;
                        }
                        Ext.Ajax.request({
                            url: OsOperacional.apiUrl('/os/' + encodeURIComponent(view.osId) + '/comentarios/' + encodeURIComponent(record.get('id'))),
                            method: 'PUT',
                            jsonData: { conteudo: value },
                            callback: function (options, success, response) {
                                if (success) {
                                    editor.close();
                                    this.carregarComentarios();
                                    view.fireEvent('comentariosalterados');
                                } else {
                                    OsOperacional.exibirErro(response, 'Não foi possível editar o comentário.');
                                }
                            },
                            scope: controller
                        });
                    },
                    scope: this
                }]
            });
            editor.show();
        },

        formatarData: function (value) {
            if (!value) {
                return '';
            }
            var data = Ext.isDate(value) ? value : (Ext.Date.parse(value, 'c') || new Date(value));
            return isNaN(data.getTime()) ? Ext.String.htmlEncode(String(value)) : Ext.Date.format(data, 'd/m/Y H:i');
        },

        podeEditarRegistro: function (record) {
            var autor = record && record.get('autor');
            return Sessao.isAdm() || (autor && String(autor.id) === String(Sessao.usuarioId));
        }
    },

    title: 'Comentários da OS',
    modal: true,
    floating: true,
    width: 920,
    height: 620,
    layout: 'fit',
    bodyPadding: 8,
    items: [{
        xtype: 'container',
        layout: { type: 'vbox', align: 'stretch' },
        items: [{
            xtype: 'grid',
            reference: 'gridComentarios',
            flex: 1,
            scrollable: true,
            store: {
                fields: [
                    { name: 'id', type: 'int' },
                    { name: 'conteudo', type: 'string' },
                    { name: 'autor' },
                    { name: 'dataCriacao' },
                    { name: 'editadoPor' },
                    { name: 'dataEdicao' }
                ],
                data: []
            },
            columns: [{
                text: 'Comentário',
                dataIndex: 'conteudo',
                flex: 5,
                cellWrap: true,
                renderer: function (value, metaData) {
                    metaData.style = 'white-space:pre-wrap;';
                    return Ext.String.htmlEncode(value || '');
                }
            }, {
                text: 'Autor',
                dataIndex: 'autor',
                flex: 2,
                renderer: function (value) {
                    return Ext.String.htmlEncode(value && value.nome ? value.nome : '');
                }
            }, {
                text: 'Criado em',
                dataIndex: 'dataCriacao',
                flex: 2,
                renderer: function (value) { return this.up('window').getController().formatarData(value); }
            }, {
                text: 'Edição',
                dataIndex: 'editadoPor',
                flex: 3,
                renderer: function (value, metaData, record) {
                    if (!value || !value.nome) {
                        return '';
                    }
                    var quando = this.up('window').getController().formatarData(record.get('dataEdicao'));
                    return Ext.String.htmlEncode(value.nome) + (quando ? ' — ' + Ext.String.htmlEncode(quando) : '');
                }
            }, {
                xtype: 'actioncolumn',
                width: 70,
                text: 'Ação',
                items: [{
                    iconCls: 'fa fa-pen',
                    tooltip: 'Editar comentário',
                    getClass: function (value, meta, record) {
                        return this.up('window').getController().podeEditarRegistro(record) ? 'fa fa-pen' : 'x-hidden';
                    },
                    handler: function (grid, rowIndex) {
                        this.up('window').getController().editarComentario(grid, rowIndex);
                    }
                }]
            }]
        }, {
            xtype: 'textarea',
            reference: 'novoComentario',
            fieldLabel: 'Novo comentário',
            labelAlign: 'top',
            emptyText: 'Escreva um comentário (máximo de 4000 caracteres)',
            maxLength: 4000,
            enforceMaxLength: true,
            allowBlank: false,
            height: 110,
            margin: '8 0 0 0'
        }]
    }],
    buttons: [{
        text: 'Fechar',
        handler: function (button) { button.up('window').close(); }
    }, {
        text: 'Comentar',
        iconCls: 'fa fa-comment',
        handler: 'criarComentario'
    }]
});
