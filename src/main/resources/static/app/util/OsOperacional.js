Ext.define('ProjSistemaOs.util.OsOperacional', {
    alternateClassName: 'OsOperacional',
    singleton: true,

    apiUrl: function (path) {
        var base = window.sistemaOsLocal && sistemaOsLocal.apiUrl ? sistemaOsLocal.apiUrl : window.location.origin;
        return base.replace(/\/$/, '') + path;
    },

    decodificar: function (response) {
        return Ext.decode(response && response.responseText ? response.responseText : '', true);
    },

    lista: function (body) {
        if (Ext.isArray(body)) {
            return body;
        }
        if (body && Ext.isArray(body.lista)) {
            return body.lista;
        }
        if (body && Ext.isArray(body.data)) {
            return body.data;
        }
        return [];
    },

    normalizarSituacao: function (value) {
        var texto = String(value || '').trim();
        if (!texto) {
            return '';
        }
        texto = texto.normalize ? texto.normalize('NFD').replace(/[\u0300-\u036f]/g, '') : texto;
        texto = texto.toUpperCase().replace(/[\s-]+/g, '_');
        if (texto === 'CONCLUIDO' || texto === 'CONCLUIDA') {
            return 'CONCLUIDO';
        }
        if (texto === 'EM_ANDAMENTO' || texto === 'EM_ANDAMENTO_') {
            return 'EM_ANDAMENTO';
        }
        if (texto === 'PENDENTE') {
            return 'PENDENTE';
        }
        if (texto === 'CANCELADO' || texto === 'CANCELADA') {
            return 'CANCELADO';
        }
        return texto;
    },

    estaEncerrada: function (os) {
        if (!os) {
            return false;
        }
        return os.encerrada === true || !!os.dataFim ||
            this.normalizarSituacao(os.situacao) === 'CONCLUIDO' ||
            this.normalizarSituacao(os.situacao) === 'CANCELADO';
    },

    exibirErro: function (response, fallback) {
        var payload = this.decodificar(response),
            mensagem = payload && (payload.mensagemErro || payload.message || payload.error);
        mensagem = mensagem || fallback || 'Não foi possível concluir a operação. Tente novamente.';
        Ext.Msg.alert('Atenção', Ext.String.htmlEncode(String(mensagem)));
    },

    carregarResponsaveis: function (callback, scope) {
        Ext.Ajax.request({
            url: this.apiUrl('/os/responsaveis'),
            method: 'GET',
            callback: function (options, success, response) {
                if (!success) {
                    this.exibirErro(response, 'Não foi possível carregar os responsáveis.');
                    if (callback) {
                        callback.call(scope || this, [], false);
                    }
                    return;
                }
                callback && callback.call(scope || this, this.lista(this.decodificar(response)), true);
            },
            scope: this
        });
    }
});
