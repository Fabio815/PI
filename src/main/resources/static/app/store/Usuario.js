Ext.define('ProjSistemaOs.store.Usuario', {
    extend: 'Ext.data.Store',
    alias: 'store.usuario-listagem-store',
    model: 'ProjSistemaOs.model.Usuario',

    remoteFilter: true,
    autoLoad: true,
    pageSize: 25,

    proxy: {
        type: 'ajax',
        url: window.location.origin + '/usuarios/listar',
        reader: {
            type: 'json',
            rootProperty: 'usuarios'
        }
    },
    listeners: {
        beforeLoad: function(store, operation){
            var filtros = store.getFilters().items;
            var arrayFiltro = [];
            for (let f of filtros) {
                let valor = f.getValue();
                arrayFiltro.push({
                    propriedade: f.getProperty(),
                    operador: f.getOperator(),
                    valor: valor
                });
            }
            store.getProxy().setExtraParams({
                filtros: Ext.encode(arrayFiltro)
            });
        }
    }
});