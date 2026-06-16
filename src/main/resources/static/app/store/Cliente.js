Ext.define('ProjSistemaOs.store.Cliente', {
    extend: 'Ext.data.Store',
    alias: 'store.cliente-listagem-store',
    model: 'ProjSistemaOs.model.Cliente',

    requires: [
        'ProjSistemaOs.util.MensagemUtil'
    ],

    //Refazer esse store
    remoteFilter: true,
    autoLoad: true,
    pageSize: 25,

    proxy: {
        type: 'ajax',
        url: window.location.origin + '/cliente/listar',
        reader: {
            type: 'json',
            rootProperty: 'clientes'
        }
    },
    listeners: {
        beforeLoad: function(store, operation){
            var filtros = store.getFilters().items;
            var arrayFiltro = [];
            console.log(filtros);
            for (let f of filtros) {
                let valor = f.getValue();
                if (f.getProperty() === "status") {
                    arrayFiltro.push ({
                        propriedade: f.getOperator(),
                        operador: 'in',
                        valor: valor
                    });
                    continue;
                }
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