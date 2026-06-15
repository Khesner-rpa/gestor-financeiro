package com.mk.gestor_financeiro.controller;

import com.mk.gestor_financeiro.controller.support.DashboardModelFactory;
import com.mk.gestor_financeiro.dto.TransacaoForm;
import com.mk.gestor_financeiro.model.Transacao;
import com.mk.gestor_financeiro.service.TransacaoService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.security.Principal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/transacoes")
public class TransacaoController {

    private static final String DASHBOARD_FRAGMENT = "dashboard :: dashboardContent";
    private static final String FORM_FRAGMENT = "dashboard :: transacaoForm";

    private final TransacaoService transacaoService;
    private final DashboardModelFactory dashboardModelFactory;

    @PostMapping
    public String criar(
            @Valid @ModelAttribute("form") TransacaoForm form,
            BindingResult result,
            Model model,
            Principal principal,
            HttpServletRequest request
    ) {
        if (!result.hasErrors()) {
            transacaoService.criar(principal.getName(), form);
            form = TransacaoForm.novo();
        }

        return renderDashboard(model, principal.getName(), form, null, request);
    }

    @GetMapping("/{id}/editar")
    public String editar(
            @PathVariable Long id,
            Model model,
            Principal principal,
            HttpServletRequest request
    ) {
        Transacao transacao = transacaoService.buscarParaEdicao(principal.getName(), id);
        dashboardModelFactory.popular(model, principal.getName(), TransacaoForm.de(transacao), id);

        return isHtmx(request) ? FORM_FRAGMENT : "dashboard";
    }

    @PostMapping("/{id}")
    public String atualizar(
            @PathVariable Long id,
            @Valid @ModelAttribute("form") TransacaoForm form,
            BindingResult result,
            Model model,
            Principal principal,
            HttpServletRequest request
    ) {
        Long editingId = id;

        if (!result.hasErrors()) {
            transacaoService.atualizar(principal.getName(), id, form);
            form = TransacaoForm.novo();
            editingId = null;
        }

        return renderDashboard(model, principal.getName(), form, editingId, request);
    }

    @GetMapping("/novo")
    public String novo(Model model, Principal principal, HttpServletRequest request) {
        dashboardModelFactory.popular(model, principal.getName(), TransacaoForm.novo(), null);

        return isHtmx(request) ? FORM_FRAGMENT : "dashboard";
    }

    @PostMapping("/{id}/excluir")
    public String excluir(
            @PathVariable Long id,
            Model model,
            Principal principal,
            HttpServletRequest request
    ) {
        transacaoService.excluir(principal.getName(), id);
        return renderDashboard(model, principal.getName(), TransacaoForm.novo(), null, request);
    }

    private String renderDashboard(
            Model model,
            String emailUsuario,
            TransacaoForm form,
            Long editingId,
            HttpServletRequest request
    ) {
        dashboardModelFactory.popular(model, emailUsuario, form, editingId);
        return isHtmx(request) ? DASHBOARD_FRAGMENT : "dashboard";
    }

    private boolean isHtmx(HttpServletRequest request) {
        return "true".equalsIgnoreCase(request.getHeader("HX-Request"));
    }
}
