package com.mk.gestor_financeiro.dto;

import java.math.BigDecimal;

public record EvolucaoMensal(
        String mes,
        BigDecimal receitas,
        BigDecimal despesas,
        BigDecimal saldo,
        int alturaReceitas,
        int alturaDespesas
) {
}
