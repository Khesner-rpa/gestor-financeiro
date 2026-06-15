package com.mk.gestor_financeiro.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record DashboardResumo(
        BigDecimal saldoAtual,
        BigDecimal receitasMes,
        BigDecimal despesasMes,
        LocalDate inicioMes,
        LocalDate fimMes
) {
}
