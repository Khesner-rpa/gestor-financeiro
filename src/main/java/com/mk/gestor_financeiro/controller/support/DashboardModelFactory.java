package com.mk.gestor_financeiro.controller.support;

import com.mk.gestor_financeiro.dto.DashboardDados;
import com.mk.gestor_financeiro.dto.PerfilForm;
import com.mk.gestor_financeiro.dto.TransacaoForm;
import com.mk.gestor_financeiro.model.CategoriaTransacao;
import com.mk.gestor_financeiro.model.TipoTransacao;
import com.mk.gestor_financeiro.model.Usuario;
import com.mk.gestor_financeiro.service.TransacaoService;
import com.mk.gestor_financeiro.service.UsuarioService;
import java.math.BigDecimal;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;

@Component
public class DashboardModelFactory {

    private final TransacaoService transacaoService;
    private final UsuarioService usuarioService;

    public DashboardModelFactory(TransacaoService transacaoService, UsuarioService usuarioService) {
        this.transacaoService = transacaoService;
        this.usuarioService = usuarioService;
    }

    public void popular(Model model, String emailUsuario) {
        popular(model, emailUsuario, TransacaoForm.novo(), null);
    }

    public void popular(Model model, String emailUsuario, TransacaoForm form, Long editingId) {
        DashboardDados dashboard = transacaoService.carregarDashboard(emailUsuario);
        Usuario usuario = usuarioService.buscarPorEmail(emailUsuario);

        model.addAttribute("resumo", dashboard.resumo());
        model.addAttribute("categoriasResumo", dashboard.categorias());
        model.addAttribute("evolucao", dashboard.evolucao());
        model.addAttribute("meta", dashboard.meta());
        model.addAttribute("insight", dashboard.insight());
        model.addAttribute("saldoNegativo", dashboard.resumo().saldoAtual().compareTo(BigDecimal.ZERO) < 0);
        model.addAttribute("principalCategoria", dashboard.categorias().isEmpty()
                ? "despesas variaveis"
                : dashboard.categorias().get(0).nome());
        model.addAttribute("usuarioNome", usuario.getNome());
        model.addAttribute("perfilForm", PerfilForm.comNome(usuario.getNome()));
        model.addAttribute("transacoes", transacaoService.listarTransacoesDoMes(emailUsuario));
        model.addAttribute("form", form);
        model.addAttribute("editingId", editingId);
        model.addAttribute("modoEdicao", editingId != null);
        model.addAttribute("emailUsuario", emailUsuario);
        model.addAttribute("tipos", TipoTransacao.values());
        model.addAttribute("categorias", CategoriaTransacao.values());
    }
}
