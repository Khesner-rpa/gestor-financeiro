package com.mk.gestor_financeiro.controller;

import com.mk.gestor_financeiro.controller.support.DashboardModelFactory;
import com.mk.gestor_financeiro.dto.TransacaoForm;
import com.mk.gestor_financeiro.model.Usuario;
import com.mk.gestor_financeiro.service.SalarioService;
import com.mk.gestor_financeiro.service.UsuarioService;
import jakarta.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.security.Principal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
@RequestMapping("/salario")
public class SalarioController {

    private static final String DASHBOARD_FRAGMENT = "dashboard :: dashboardContent";

    private final SalarioService salarioService;
    private final UsuarioService usuarioService;
    private final DashboardModelFactory dashboardModelFactory;

    @PostMapping("/definir")
    public String definir(
            @RequestParam BigDecimal valor,
            @RequestParam(required = false) Integer dia,
            @RequestParam(required = false) Boolean fixo,
            Principal principal,
            Model model,
            HttpServletRequest request
    ) {
        Usuario usuario = usuarioService.buscarPorEmail(principal.getName());
        try {
            salarioService.definir(usuario, valor, dia, fixo != null && fixo);
        } catch (IllegalArgumentException e) {
            model.addAttribute("erroSalario", e.getMessage());
        }
        return renderDashboard(model, principal.getName(), request);
    }

    @PostMapping("/comissao")
    public String adicionarComissao(
            @RequestParam BigDecimal valor,
            Principal principal,
            Model model,
            HttpServletRequest request
    ) {
        Usuario usuario = usuarioService.buscarPorEmail(principal.getName());
        try {
            salarioService.adicionarComissao(usuario, valor);
        } catch (IllegalArgumentException e) {
            model.addAttribute("erroSalario", e.getMessage());
        }
        return renderDashboard(model, principal.getName(), request);
    }

    private String renderDashboard(Model model, String emailUsuario, HttpServletRequest request) {
        dashboardModelFactory.popular(model, emailUsuario, TransacaoForm.novo(), null);
        return "true".equalsIgnoreCase(request.getHeader("HX-Request")) ? DASHBOARD_FRAGMENT : "dashboard";
    }
}