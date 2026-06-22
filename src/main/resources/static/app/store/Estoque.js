Ext.define('ProjSistemaOs.store.Estoque', {
    extend: 'Ext.data.Store',
    alias: 'store.estoque-listagem-store',
    model: 'ProjSistemaOs.model.Estoque',

    data: [
        { id: 1, nome: 'Pneu Aro 29', qtd: 15, valor_unitario: 120.50, statusPeca: true },
        { id: 2, nome: 'Câmara de Ar Aro 29', qtd: 30, valor_unitario: 25.00, statusPeca: true },
        { id: 3, nome: 'Corrente 9 Velocidades', qtd: 12, valor_unitario: 89.90, statusPeca: true },
        { id: 4, nome: 'Cassete 11-36D', qtd: 8, valor_unitario: 180.00, statusPeca: true },
        { id: 5, nome: 'Pastilha de Freio a Disco', qtd: 25, valor_unitario: 35.00, statusPeca: true },
        { id: 6, nome: 'Disco de Freio 160mm', qtd: 10, valor_unitario: 75.00, statusPeca: true },
        { id: 7, nome: 'Câmbio Traseiro Shimano', qtd: 6, valor_unitario: 250.00, statusPeca: true },
        { id: 8, nome: 'Pedivela Alumínio', qtd: 5, valor_unitario: 320.00, statusPeca: true },
        { id: 9, nome: 'Guidão MTB', qtd: 9, valor_unitario: 95.50, statusPeca: true },
        { id: 10, nome: 'Selim Esportivo', qtd: 4, valor_unitario: 110.00, statusPeca: false }
    ]
});