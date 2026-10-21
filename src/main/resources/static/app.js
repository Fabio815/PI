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
    mainView: 'ProjSistemaOs.view.login.LoginPanel'
    //mainView: 'ProjSistemaOs.view.main.Main'
});
// Menu "Filtros" que aparece no menu da coluna
Ext.define('ProjSistemaOs.overrides.FiltersPtBr', {
    override: 'Ext.grid.filters.Filters',
    menuFilterText: 'Filtros'
});

// Filtro de texto
Ext.define('ProjSistemaOs.overrides.FilterStringPtBr', {
    override: 'Ext.grid.filters.filter.String',
    emptyText: 'Digite o texto do filtro...'
});

// Filtro de data (Antes / Depois / Em)
Ext.define('ProjSistemaOs.overrides.FilterDatePtBr', {
    override: 'Ext.grid.filters.filter.Date',
    fields: {
        lt: { text: 'Antes de' },
        gt: { text: 'Depois de' },
        eq: { text: 'Em' }
    }
});

// Itens do menu da coluna (ordenação e colunas)
Ext.define('ProjSistemaOs.overrides.ColumnPtBr', {
    override: 'Ext.grid.column.Column',
    sortAscText: 'Ordenar crescente',
    sortDescText: 'Ordenar decrescente',
    columnsText: 'Colunas'
});