<<<<<<< HEAD
package com.mk.gestor_financeiro.controller;

import com.mk.gestor_financeiro.model.Usuario;
import com.mk.gestor_financeiro.service.ReservaService;
import com.mk.gestor_financeiro.service.UsuarioService;
import com.mk.gestor_financeiro.service.TransacaoService;
import com.mk.gestor_financeiro.controller.support.DashboardModelFactory;
import com.mk.gestor_financeiro.dto.TransacaoForm;
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
@RequestMapping("/reserva")
public class ReservaController {

    private final ReservaService reservaService;
    private final UsuarioService usuarioService;
    private final TransacaoService transacaoService;
    private final DashboardModelFactory dashboardModelFactory;

    @PostMapping("/depositar")
    public String depositar(@RequestParam BigDecimal valor, Principal principal, Model model, HttpServletRequest request) {
        Usuario usuario = usuarioService.buscarPorEmail(principal.getName());
        BigDecimal saldoAtual = transacaoService.calcularResumoDoMes(usuario.getEmail()).saldoAtual();
        try {
            reservaService.depositar(usuario, valor, saldoAtual);
        } catch (IllegalArgumentException e) {
            model.addAttribute("erroReserva", e.getMessage());
        }
        return renderDashboard(model, principal.getName(), request);
    }

    @PostMapping("/resgatar")
    public String resgatar(@RequestParam BigDecimal valor, Principal principal, Model model, HttpServletRequest request) {
        Usuario usuario = usuarioService.buscarPorEmail(principal.getName());
        try {
            reservaService.resgatar(usuario, valor);
        } catch (IllegalArgumentException e) {
            model.addAttribute("erroReserva", e.getMessage());
        }
        return renderDashboard(model, principal.getName(), request);
    }

    @PostMapping("/objetivo")
    public String atualizarObjetivo(@RequestParam BigDecimal objetivo, Principal principal, Model model, HttpServletRequest request) {
        Usuario usuario = usuarioService.buscarPorEmail(principal.getName());
        try {
            reservaService.atualizarObjetivo(usuario, objetivo);
        } catch (IllegalArgumentException e) {
            model.addAttribute("erroReserva", e.getMessage());
        }
        return renderDashboard(model, principal.getName(), request);
    }

    private String renderDashboard(Model model, String emailUsuario, HttpServletRequest request) {
        dashboardModelFactory.popular(model, emailUsuario, TransacaoForm.novo(), null);
        return "true".equalsIgnoreCase(request.getHeader("HX-Request")) ? "dashboard :: dashboardContent" : "dashboard";
    }
}
=======
package com.mk.gestor_financeiro.controller;

import com.mk.gestor_financeiro.model.Usuario;
import com.mk.gestor_financeiro.service.ReservaService;
import com.mk.gestor_financeiro.service.UsuarioService;
import com.mk.gestor_financeiro.service.TransacaoService;
import com.mk.gestor_financeiro.controller.support.DashboardModelFactory;
import com.mk.gestor_financeiro.dto.TransacaoForm;
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
@RequestMapping("/reserva")
public class ReservaController {

    private final ReservaService reservaService;
    private final UsuarioService usuarioService;
    private final TransacaoService transacaoService;
    private final DashboardModelFactory dashboardModelFactory;

    @PostMapping("/depositar")
    public String depositar(@RequestParam BigDecimal valor, Principal principal, Model model, HttpServletRequest request) {
        Usuario usuario = usuarioService.buscarPorEmail(principal.getName());
        BigDecimal saldoAtual = transacaoService.calcularResumoDoMes(usuario.getEmail()).saldoAtual();
        try {
            reservaService.depositar(usuario, valor, saldoAtual);
        } catch (IllegalArgumentException e) {
            model.addAttribute("erroReserva", e.getMessage());
        }
        return renderDashboard(model, principal.getName(), request);
    }

    @PostMapping("/resgatar")
    public String resgatar(@RequestParam BigDecimal valor, Principal principal, Model model, HttpServletRequest request) {
        Usuario usuario = usuarioService.buscarPorEmail(principal.getName());
        try {
            reservaService.resgatar(usuario, valor);
        } catch (IllegalArgumentException e) {
            model.addAttribute("erroReserva", e.getMessage());
        }
        return renderDashboard(model, principal.getName(), request);
    }

    private String renderDashboard(Model model, String emailUsuario, HttpServletRequest request) {
        dashboardModelFactory.popular(model, emailUsuario, TransacaoForm.novo(), null);
        return "true".equalsIgnoreCase(request.getHeader("HX-Request")) ? "dashboard :: dashboardContent" : "dashboard";
    }
}
>>>>>>> 6be877a264aef21474bf1ccbbf9c660e2ac5dcce
