Ext.define('ProjSistemaOs.store.Os', {
    extend: 'Ext.data.Store',
    alias: 'store.os-listagem-store',
    model: 'ProjSistemaOs.model.Os',

    remoteFilter: true,
    autoLoad: true,
    pageSize: 15,

    proxy: {
        type: 'ajax',

        url: window.location.origin + '/os/listar',

        reader: {
            type: 'json',
            rootProperty: 'listaOs',
            totalProperty: 'total'
        }
    },
    listeners: {
        beforeLoad: function(store) {
            var filtros = store.getFilters().items;
            var params = {};

            for (let f of filtros) {
                let propriedade = f.getProperty();
                let valor = f.getValue();
                if (propriedade === 'nomeCliente') {
                    params.nome = valor;
                }
                if (propriedade === 'id') {
                    params.id = valor;
                }
                if (propriedade === 'status') {
                    params.status = valor;
                }
            }
            store.getProxy().setExtraParams(params);
            console.log(params);
        }
    }
});