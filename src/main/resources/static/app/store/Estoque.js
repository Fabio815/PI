Ext.define('ProjSistemaOs.store.Estoque', {
    extend: 'Ext.data.Store',
    alias: 'store.estoque-listagem-store',
    model: 'ProjSistemaOs.model.Estoque',

    data: [
        { id: 1, nome: 'HD 1TB', qtd: 15, valor_unitario: 320.50, statusPeca: true },
        { id: 2, nome: 'SSD 480GB', qtd: 8, valor_unitario: 280.00, statusPeca: true },
        { id: 3, nome: 'Memória RAM 8GB', qtd: 20, valor_unitario: 150.90, statusPeca: true },
        { id: 4, nome: 'Memória RAM 16GB', qtd: 10, valor_unitario: 299.90, statusPeca: true },
        { id: 5, nome: 'Fonte 500W', qtd: 5, valor_unitario: 210.00, statusPeca: true },
        { id: 6, nome: 'Placa de Vídeo GTX 1650', qtd: 3, valor_unitario: 1200.00, statusPeca: true },
        { id: 7, nome: 'Placa Mãe B450', qtd: 7, valor_unitario: 650.00, statusPeca: true },
        { id: 8, nome: 'Processador Ryzen 5', qtd: 4, valor_unitario: 950.00, statusPeca: true },
        { id: 9, nome: 'Cabo SATA', qtd: 30, valor_unitario: 12.50, statusPeca: true },
        { id: 10, nome: 'Cooler para CPU', qtd: 12, valor_unitario: 85.75, statusPeca: false }
    ]
});