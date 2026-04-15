Ext.define('ProjSistemaOs.model.Cliente', {
    extend: 'Ext.data.Model',

    idProperty: 'id',

    fields: [
        { name: 'id', type: 'int' },
        { name: 'nome', type: 'string' },
        { name: 'telefone', type: 'string' },
        { name: 'status',
            convert: function (valor) {
                return valor === 'ATIVO' || valor === true;
            }
        },


        { name: 'rua', mapping: 'endereco.rua' },
        { name: 'logradouro', mapping: 'endereco.logradouro' },
        { name: 'numero', mapping: 'endereco.numero' },
        { name: 'complemento', mapping: 'endereco.complemento' },
    ],
    //Aqui é o relacionamento com o endereco e o associationKey: 'endereco' serve para colocar
    //como nome o de associação o endereco senão fica o name: 'endereco'
    hasOne: {
        model: 'ProjSistemaOs.model.Endereco',
        name: 'endereco',
        //associationKey: 'endereco'
    }
});