<<<<<<< HEAD
package com.mk.gestor_financeiro.model;

public enum CategoriaTransacao {
    ALIMENTACAO("Alimentacao"),
    MORADIA("Moradia"),
    TRANSPORTE("Transporte"),
    SAUDE("Saude"),
    EDUCACAO("Educação"),
    LAZER("Lazer"),
    SALARIO("Salario"),
    INVESTIMENTOS("Investimentos"),
    OUTROS("Outros");

    private final String descricao;

    CategoriaTransacao(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
=======
package com.mk.gestor_financeiro.model;

public enum CategoriaTransacao {
    ALIMENTACAO("Alimentacao"),
    MORADIA("Moradia"),
    TRANSPORTE("Transporte"),
    SAUDE("Saude"),
    EDUCACAO("Educação"),
    LAZER("Lazer"),
    SALARIO("Salario"),
    INVESTIMENTOS("Investimentos"),
    OUTROS("Outros");

    private final String descricao;

    CategoriaTransacao(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
>>>>>>> 6be877a264aef21474bf1ccbbf9c660e2ac5dcce
