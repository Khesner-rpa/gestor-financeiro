package com.mk.gestor_financeiro.controller;

import com.mk.gestor_financeiro.controller.support.DashboardModelFactory;
import com.mk.gestor_financeiro.service.TransacaoService;
import jakarta.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.time.YearMonth;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class DashboardController {

    private final DashboardModelFactory dashboardModelFactory;
    private final TransacaoService transacaoService;

    public DashboardController(DashboardModelFactory dashboardModelFactory, TransacaoService transacaoService) {
        this.dashboardModelFactory = dashboardModelFactory;
        this.transacaoService = transacaoService;
    }

    @GetMapping("/dashboard")
    public String dashboard(
            Model model,
            Principal principal,
            @RequestParam(required = false) Integer mes,
            @RequestParam(required = false) Integer ano,
            HttpServletRequest request
    ) {
        List<YearMonth> mesesComTransacoes = transacaoService.listarMesesComTransacoes(principal.getName());
        YearMonth mesSelecionado = resolverMes(mes, ano, mesesComTransacoes);
        dashboardModelFactory.popular(model, principal.getName(), mesSelecionado);

        return isHtmx(request) ? "dashboard :: dashboardContent" : "dashboard";
    }

    private YearMonth resolverMes(Integer mes, Integer ano, List<YearMonth> disponiveis) {
        YearMonth pedido = null;

        if (ano != null && ano >= 1900 && mes != null && mes >= 1 && mes <= 12) {
            try {
                pedido = YearMonth.of(ano, mes);
            } catch (Exception e) {
                pedido = null;
            }
        }

        if (pedido == null) {
            pedido = YearMonth.now();
        }

        return ajustarParaDisponibilidade(pedido, disponiveis);
    }

    private YearMonth ajustarParaDisponibilidade(YearMonth pedido, List<YearMonth> disponiveis) {
        if (disponiveis.isEmpty() || disponiveis.contains(pedido)) {
            return pedido;
        }

        return disponiveis.stream()
                .filter(mes -> mes.getYear() == pedido.getYear())
                .max(YearMonth::compareTo)
                .orElse(disponiveis.get(disponiveis.size() - 1));
    }

    private boolean isHtmx(HttpServletRequest request) {
        return "true".equalsIgnoreCase(request.getHeader("HX-Request"));
    }
}