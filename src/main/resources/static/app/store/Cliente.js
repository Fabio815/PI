Ext.define('ProjSistemaOs.store.Cliente', {
    extend: 'Ext.data.Store',
    alias: 'store.cliente-listagem-store',

    model: 'ProjSistemaOs.model.Cliente',

    requires: [
        'ProjSistemaOs.util.MensagemUtil'
    ],
    remoteFilter: true,
    autoLoad: true,
    pageSize: 25,

    proxy: {
        type: 'ajax',
        url: window.location.origin + '/cliente/listar',
        method: 'GET',

        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    },
    listeners: {
        beforeLoad: function(store) {
            var filtros = store.getFilters().items;
            var params = {};
            for (let f of filtros) {
                let propriedade = f.getProperty();
                let valor = f.getValue();
                switch (propriedade) {
                    case 'id':
                        params.id = valor;
                        break;
                    case 'nome':
                        params.nome = valor;
                        break
                    case 'status':
                        params.status = valor;
                        break;
                    default:
                        break;
                }
            }
            store.getProxy().setExtraParams(params);
        }
    }
});