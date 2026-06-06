Ext.define('ProjSistemaOs.store.Usuario', {
    extend: 'Ext.data.Store',
    alias: 'store.usuario-listagem-store',
    model: 'ProjSistemaOs.model.Usuario',

    remoteFilter: true,
    autoLoad: true,
    pageSize: 25,

    proxy: {
        type: 'ajax',
        url: 'http://localhost:8080/usuarios/listar',
        reader: {
            type: 'json',
            rootProperty: 'usuarios'
        }
    },
    /*listeners: {
        beforeLoad: function(store, operation){
            store.getProxy().setExtraParams({
                filtros: Ext.encode(arrayFiltro)
            });
        }
    }*/
});