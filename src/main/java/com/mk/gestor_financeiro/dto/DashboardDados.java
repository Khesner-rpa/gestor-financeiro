package com.mk.gestor_financeiro.dto;

import java.util.List;

public record DashboardDados(
        DashboardResumo resumo,
        List<CategoriaResumo> categorias,
        List<EvolucaoMensal> evolucao,
        MetaFinanceira meta,
        InsightFinanceiro insight
) {
}
