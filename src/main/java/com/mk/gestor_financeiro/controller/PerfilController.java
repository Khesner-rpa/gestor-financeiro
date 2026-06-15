<<<<<<< HEAD
package com.mk.gestor_financeiro.controller;

import com.mk.gestor_financeiro.controller.support.DashboardModelFactory;
import com.mk.gestor_financeiro.dto.PerfilForm;
import com.mk.gestor_financeiro.service.UsuarioService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.security.Principal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/perfil")
public class PerfilController {

    private static final String DASHBOARD_FRAGMENT = "dashboard :: dashboardContent";

    private final UsuarioService usuarioService;
    private final DashboardModelFactory dashboardModelFactory;

    public PerfilController(UsuarioService usuarioService, DashboardModelFactory dashboardModelFactory) {
        this.usuarioService = usuarioService;
        this.dashboardModelFactory = dashboardModelFactory;
    }

    @PostMapping
    public String atualizar(
            @Valid @ModelAttribute("perfilForm") PerfilForm form,
            BindingResult result,
            Model model,
            Principal principal,
            HttpServletRequest request
    ) {
        if (!result.hasErrors()) {
            usuarioService.atualizarNome(principal.getName(), form.getNome());
        }

        dashboardModelFactory.popular(model, principal.getName());

        if (result.hasErrors()) {
            model.addAttribute("perfilForm", form);
        }

        return isHtmx(request) ? DASHBOARD_FRAGMENT : "dashboard";
    }

    private boolean isHtmx(HttpServletRequest request) {
        return "true".equalsIgnoreCase(request.getHeader("HX-Request"));
    }
}
=======
package com.mk.gestor_financeiro.controller;

import com.mk.gestor_financeiro.controller.support.DashboardModelFactory;
import com.mk.gestor_financeiro.dto.PerfilForm;
import com.mk.gestor_financeiro.service.UsuarioService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.security.Principal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/perfil")
public class PerfilController {

    private static final String DASHBOARD_FRAGMENT = "dashboard :: dashboardContent";

    private final UsuarioService usuarioService;
    private final DashboardModelFactory dashboardModelFactory;

    public PerfilController(UsuarioService usuarioService, DashboardModelFactory dashboardModelFactory) {
        this.usuarioService = usuarioService;
        this.dashboardModelFactory = dashboardModelFactory;
    }

    @PostMapping
    public String atualizar(
            @Valid @ModelAttribute("perfilForm") PerfilForm form,
            BindingResult result,
            Model model,
            Principal principal,
            HttpServletRequest request
    ) {
        if (!result.hasErrors()) {
            usuarioService.atualizarNome(principal.getName(), form.getNome());
        }

        dashboardModelFactory.popular(model, principal.getName());

        if (result.hasErrors()) {
            model.addAttribute("perfilForm", form);
        }

        return isHtmx(request) ? DASHBOARD_FRAGMENT : "dashboard";
    }

    private boolean isHtmx(HttpServletRequest request) {
        return "true".equalsIgnoreCase(request.getHeader("HX-Request"));
    }
}
>>>>>>> 6be877a264aef21474bf1ccbbf9c660e2ac5dcce
