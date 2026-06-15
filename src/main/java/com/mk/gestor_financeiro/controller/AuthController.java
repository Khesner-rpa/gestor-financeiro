<<<<<<< HEAD
package com.mk.gestor_financeiro.controller;

import com.mk.gestor_financeiro.dto.CadastroForm;
import com.mk.gestor_financeiro.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AuthController {

    private final UsuarioService usuarioService;

    public AuthController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping("/")
    public String inicio(Authentication authentication) {
        if (isUsuarioLogado(authentication)) {
            return "redirect:/dashboard";
        }

        return "redirect:/login";
    }

    @GetMapping("/login")
    public String login(Authentication authentication) {
        if (isUsuarioLogado(authentication)) {
            return "redirect:/dashboard";
        }

        return "login";
    }

    @GetMapping("/cadastro")
    public String cadastro(Model model, Authentication authentication) {
        if (isUsuarioLogado(authentication)) {
            return "redirect:/dashboard";
        }

        if (!model.containsAttribute("cadastroForm")) {
            model.addAttribute("cadastroForm", new CadastroForm());
        }

        return "cadastro";
    }

    @PostMapping("/cadastro")
    public String cadastrar(
            @Valid @ModelAttribute("cadastroForm") CadastroForm form,
            BindingResult result
    ) {
        if (result.hasErrors()) {
            return "cadastro";
        }

        try {
            usuarioService.cadastrar(form);
            return "redirect:/login?cadastro";
        } catch (IllegalArgumentException exception) {
            result.rejectValue("email", "email.duplicado", exception.getMessage());
            return "cadastro";
        }
    }

    private boolean isUsuarioLogado(Authentication authentication) {
        return authentication != null
                && authentication.isAuthenticated()
                && !(authentication instanceof AnonymousAuthenticationToken);
    }
}
=======
package com.mk.gestor_financeiro.controller;

import com.mk.gestor_financeiro.dto.CadastroForm;
import com.mk.gestor_financeiro.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AuthController {

    private final UsuarioService usuarioService;

    public AuthController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping("/")
    public String inicio(Authentication authentication) {
        if (isUsuarioLogado(authentication)) {
            return "redirect:/dashboard";
        }

        return "redirect:/login";
    }

    @GetMapping("/login")
    public String login(Authentication authentication) {
        if (isUsuarioLogado(authentication)) {
            return "redirect:/dashboard";
        }

        return "login";
    }

    @GetMapping("/cadastro")
    public String cadastro(Model model, Authentication authentication) {
        if (isUsuarioLogado(authentication)) {
            return "redirect:/dashboard";
        }

        if (!model.containsAttribute("cadastroForm")) {
            model.addAttribute("cadastroForm", new CadastroForm());
        }

        return "cadastro";
    }

    @PostMapping("/cadastro")
    public String cadastrar(
            @Valid @ModelAttribute("cadastroForm") CadastroForm form,
            BindingResult result
    ) {
        if (result.hasErrors()) {
            return "cadastro";
        }

        try {
            usuarioService.cadastrar(form);
            return "redirect:/login?cadastro";
        } catch (IllegalArgumentException exception) {
            result.rejectValue("email", "email.duplicado", exception.getMessage());
            return "cadastro";
        }
    }

    private boolean isUsuarioLogado(Authentication authentication) {
        return authentication != null
                && authentication.isAuthenticated()
                && !(authentication instanceof AnonymousAuthenticationToken);
    }
}
>>>>>>> 6be877a264aef21474bf1ccbbf9c660e2ac5dcce
