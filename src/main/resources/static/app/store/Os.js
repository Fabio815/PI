Ext.define('ProjSistemaOs.store.Os', {
    extend: 'Ext.data.Store',
    alias: 'store.os-listagem-store',
    model: 'ProjSistemaOs.model.Os',

    data: [{
            id: 1,
            dataInicio: '2026-07-01',
            nomeCliente: 'João Silva',
            telefone: '(47) 99999-1111',
            preco: 350.00,
            situacao: 'Em Andamento',
            dataFim: '',
            status: 'Ativo'
        }, {
            id: 2,
            dataInicio: '2026-06-15',
            nomeCliente: 'Maria Souza',
            telefone: '(47) 98888-2222',
            preco: 1200.50,
            situacao: 'Concluído',
            dataFim: '2026-06-20',
            status: 'ATIVO'
        }, {
            id: 3,
            dataInicio: '2026-05-20',
            nomeCliente: 'Carlos Oliveira',
            telefone: '(47) 97777-3333',
            preco: 780.90,
            situacao: 'Concluído',
            dataFim: '2026-06-01',
            status: 'INATIVO'
        }, {
            id: 4,
            dataInicio: '2026-07-05',
            nomeCliente: 'Ana Pereira',
            telefone: '(47) 96666-4444',
            preco: 499.99,
            situacao: 'Cancelado',
            dataFim: '2026-07-08',
            status: 'INATIVO'
        }, {
            id: 5,
            dataInicio: '2026-07-12',
            nomeCliente: 'Lucas Martins',
            telefone: '(47) 95555-5555',
            preco: 250.75,
            situacao: 'Em Andamento',
            dataFim: '',
            status: 'ATIVO'
    }]
});