package com.mk.gestor_financeiro.dto;

import java.math.BigDecimal;

public record CategoriaResumo(
        String nome,
        BigDecimal valor,
        BigDecimal percentual
) {
}