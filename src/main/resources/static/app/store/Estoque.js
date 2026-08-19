Ext.define('ProjSistemaOs.store.Estoque', {
    extend: 'Ext.data.Store',
    alias: 'store.estoque-listagem-store',

    model: 'ProjSistemaOs.model.Estoque',

    remoteFilter: true,
    autoLoad: true,
    pageSize: 25,

    proxy: {
        type: 'ajax',

        url: window.location.origin + '/produto/listar',

        pageParam: 'page',
        limitParam: 'size',

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
                if (propriedade === 'nome') {
                    params.nome = valor;
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