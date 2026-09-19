/**
 * This class is the controller for the main view for the application. It is specified as
 * the "controller" of the Main view class.
 */
Ext.define('ProjSistemaOs.view.main.MainController', {
    extend: 'Ext.app.ViewController',

    alias: 'controller.main',

    requires: ['ProjSistemaOs.util.SessaoUtil'],

    onItemSelected: function (sender, record) {
        Ext.Msg.confirm('Confirm', 'Are you sure?', 'onConfirm', this);
    },

    onConfirm: function (choice) {
        if (choice === 'yes') {
            //
        }
    },

    logout: function() {
        var me = this;
        Ext.Ajax.request({
            url: sistemaOsLocal.apiUrl + '/auth/logout',
            method: 'POST',
            success: function() {
                SessaoUtil.limparSessao();
                window.location.href = '/';
            },
            failure: function() {
                SessaoUtil.limparSessao();
                window.location.href = '/';
            }
        });
    }
});
