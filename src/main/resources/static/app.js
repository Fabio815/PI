/*
 * This file launches the application by asking Ext JS to create
 * and launch() the Application class.
 */
Ext.application({
    extend: 'ProjSistemaOs.Application',

    name: 'ProjSistemaOs',

    requires: [
        // This will automatically load all classes in the ProjSistemaOs namespace
        // so that application classes do not need to require each other.
        'ProjSistemaOs.*'
    ],

    // The name of the initial view to create.
    mainView: 'ProjSistemaOs.view.main.Main'
    //mainView: 'ProjSistemaOs.view.login.LoginPanel'
});
