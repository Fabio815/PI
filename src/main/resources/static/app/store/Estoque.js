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
    }
});