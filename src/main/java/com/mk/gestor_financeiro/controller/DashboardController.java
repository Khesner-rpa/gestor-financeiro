<<<<<<< HEAD
package com.mk.gestor_financeiro.controller;

import com.mk.gestor_financeiro.controller.support.DashboardModelFactory;
import java.security.Principal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    private final DashboardModelFactory dashboardModelFactory;

    public DashboardController(DashboardModelFactory dashboardModelFactory) {
        this.dashboardModelFactory = dashboardModelFactory;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model, Principal principal) {
        dashboardModelFactory.popular(model, principal.getName());
        return "dashboard";
    }
}
=======
package com.mk.gestor_financeiro.controller;

import com.mk.gestor_financeiro.controller.support.DashboardModelFactory;
import java.security.Principal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    private final DashboardModelFactory dashboardModelFactory;

    public DashboardController(DashboardModelFactory dashboardModelFactory) {
        this.dashboardModelFactory = dashboardModelFactory;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model, Principal principal) {
        dashboardModelFactory.popular(model, principal.getName());
        return "dashboard";
    }
}
>>>>>>> 6be877a264aef21474bf1ccbbf9c660e2ac5dcce
