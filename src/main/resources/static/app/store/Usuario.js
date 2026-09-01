Ext.define('ProjSistemaOs.store.Usuario', {
    extend: 'Ext.data.Store',
    alias: 'store.usuario-listagem-store',
    model: 'ProjSistemaOs.model.Usuario',

    remoteFilter: true,
    autoLoad: true,
    pageSize: 15,

    proxy: {
        type: 'ajax',
        url: window.location.origin + '/usuarios/listar',
        method: 'GET',
        reader: {
            type: 'json',
            rootProperty: 'listaUsuarios',
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
                    case 'email':
                        params.email = valor;
                        break;
                    default:
                        break;
                }
            }
            store.getProxy().setExtraParams(params);
        }
    }
});