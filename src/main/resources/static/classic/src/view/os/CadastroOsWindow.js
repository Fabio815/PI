Ext.define('ProjSistemaOs.view.os.CadastroOsWindow', {
    extend: 'Ext.panel.Panel',
    xtype: 'cadastro-os-panel',

    title: 'Cadastro Os',
    layout: 'fit',
    resizable: false,
    width: 650,
    bodyPadding: 15,
    ui: 'light',
    padding: 5,
    shadow: true,
    style: {
        backgroundColor: "#ececec",
        borderRadius: '5px'
    },
    header: {
        style: {
            backgroundColor: "#ececec"
        }
    },
});