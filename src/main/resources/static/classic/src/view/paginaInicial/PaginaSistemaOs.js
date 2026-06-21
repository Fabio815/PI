Ext.define('ProjSistemaOs.view.paginaInicial.PaginaSistemaOs', {
    extend: 'Ext.form.Panel',
    xtype: 'pagina-inicial-panel',

    title: 'Bem vindo ao Sistema',
    layout: {
        type: 'vbox',
        pack: 'center',
        align: 'center'
    },
    items: [{
        xtype: 'component',
        html: 'Bem-vindo ao Sistema'
    }]
});