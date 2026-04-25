Ext.define('ProjSistemaOs.store.Cliente', {
    extend: 'Ext.data.Store',
    alias: 'store.cliente-listagem-store',
    model: 'ProjSistemaOs.model.Cliente',

    remoteFilter: true,
    autoLoad: true,
    pageSize: 25,

    proxy: {
        type: 'ajax',
        url: 'http://localhost:8080/cliente/listar',
        reader: {
            type: 'json',
            rootProperty: 'clientes'
        }
    },
    listeners: {
        beforeLoad: function(store, operation){
            var filtros = store.getFilters().items;
            var arrayFiltro = [];

            for (var f of filtros) {
                let valor = f.getValue();
                if (f.getProperty() === "status") {
                    arrayFiltro.push ({
                        propriedade: 'checkcolumn',
                        operador: 'in',
                        valor: valor === 1 ? "ATIVO" : "INATIVO"
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