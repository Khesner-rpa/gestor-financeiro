package com.mk.gestor_financeiro.model;

public enum CategoriaTransacao {
    ALIMENTACAO("Alimentacao"),
    MORADIA("Moradia"),
    TRANSPORTE("Transporte"),
    SAUDE("Saude"),
    EDUCACAO("Educação"),
    LAZER("Lazer"),
    SALARIO("Salario"),
    COMISSAO("Comissao"),
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
