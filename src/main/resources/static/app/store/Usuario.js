Ext.define('ProjSistemaOs.store.Usuario', {
    extend: 'Ext.data.Store',
    alias: 'store.usuario-listagem-store',
    model: 'ProjSistemaOs.model.Usuario',

    data: [
      { id: 1, chave: "Administrador", nome: "Ana Silva", email: "ana.silva@email.com"},
      { id: 2, chave: "Funcionário", nome: "Bruno Costa", email: "bruno.costa@email.com" },
      { id: 3, chave: "Funcionário", nome: "Carla Souza", email: "carla.souza@email.com" },
      { id: 4, chave: "Funcionário", nome: "Diego Martins", email: "diego.martins@email.com" },
      { id: 5, chave: "Funcionário", nome: "Eduarda Lima", email: "eduarda.lima@email.com" },
      { id: 6, chave: "Funcionário", nome: "Felipe Rocha", email: "felipe.rocha@email.com" },
      { id: 7, chave: "Funcionário", nome: "Gabriela Alves", email: "gabriela.alves@email.com" },
      { id: 8, chave: "Funcionário", nome: "Henrique Melo", email: "henrique.melo@email.com" },
      { id: 9, chave: "Funcionário", nome: "Isabela Ferreira", email: "isabela.ferreira@email.com" },
      { id: 10, chave: "Funcionário", nome: "João Pereira", email: "joao.pereira@email.com" }
    ]
});