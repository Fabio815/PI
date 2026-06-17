Ext.define('ProjSistemaOs.store.Os', {
    extend: 'Ext.data.Store',
    alias: 'store.estoque-listagem-store',
    model: 'ProjSistemaOs.model.Os',

    data: [
        {
            id: 1,
            dataInicio: '2026-07-01',
            nomeCliente: 'João Silva',
            telefone: '(47) 99999-1111',
            preco: 350.00,
            situacao: 'Em Andamento',
            dataFim: '2026-07-10',
            status: 'Ativo',
            _status: 'I'
        },
        {
            id: 2,
            dataInicio: '2026-06-15',
            nomeCliente: 'Maria Souza',
            telefone: '(47) 98888-2222',
            preco: 1200.50,
            situacao: 'Concluído',
            dataFim: '2026-06-20',
            status: 'ATIVO',
            _status: 'I'
        },
        {
            id: 3,
            dataInicio: '2026-05-20',
            nomeCliente: 'Carlos Oliveira',
            telefone: '(47) 97777-3333',
            preco: 780.90,
            situacao: 'Atrasado',
            dataFim: '2026-06-01',
            status: 'INATIVO',
            _status: 'I'
        },
        {
            id: 4,
            dataInicio: '2026-07-05',
            nomeCliente: 'Ana Pereira',
            telefone: '(47) 96666-4444',
            preco: 499.99,
            situacao: 'Cancelado',
            dataFim: '2026-07-08',
            status: 'INATIVO',
            _status: 'I'
        },
        {
            id: 5,
            dataInicio: '2026-07-12',
            nomeCliente: 'Lucas Martins',
            telefone: '(47) 95555-5555',
            preco: 250.75,
            situacao: 'Em Andamento',
            dataFim: '2026-07-18',
            status: 'ATIVO',
            _status: 'I'
        }
    ]
});