package com.mk.gestor_financeiro.dto;

import java.math.BigDecimal;

public record MetaFinanceira(
        String nome,
        BigDecimal objetivo,
        BigDecimal acumulado,
        int percentual,
        BigDecimal restante
) {
}
