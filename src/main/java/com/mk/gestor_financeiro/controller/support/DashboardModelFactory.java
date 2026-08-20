package com.mk.gestor_financeiro.controller.support;

import com.mk.gestor_financeiro.dto.DashboardDados;
import com.mk.gestor_financeiro.dto.MesDisponivel;
import com.mk.gestor_financeiro.dto.PerfilForm;
import com.mk.gestor_financeiro.dto.TransacaoForm;
import com.mk.gestor_financeiro.model.CategoriaTransacao;
import com.mk.gestor_financeiro.model.Salario;
import com.mk.gestor_financeiro.model.TipoTransacao;
import com.mk.gestor_financeiro.model.Usuario;
import com.mk.gestor_financeiro.service.SalarioService;
import com.mk.gestor_financeiro.service.TransacaoService;
import com.mk.gestor_financeiro.service.UsuarioService;
import java.math.BigDecimal;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.IntStream;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;

@Component
public class DashboardModelFactory {

    private final TransacaoService transacaoService;
    private final UsuarioService usuarioService;
    private final SalarioService salarioService;

    public DashboardModelFactory(TransacaoService transacaoService, UsuarioService usuarioService, SalarioService salarioService) {
        this.transacaoService = transacaoService;
        this.usuarioService = usuarioService;
        this.salarioService = salarioService;
    }

    public void popular(Model model, String emailUsuario) {
        popular(model, emailUsuario, YearMonth.now());
    }

    public void popular(Model model, String emailUsuario, TransacaoForm form, Long editingId) {
        popular(model, emailUsuario, form, editingId, YearMonth.now());
    }

    public void popular(Model model, String emailUsuario, YearMonth mesSelecionado) {
        popular(model, emailUsuario, TransacaoForm.novo(), null, mesSelecionado);
    }

    public void popular(Model model, String emailUsuario, YearMonth mesSelecionado, TransacaoForm form, Long editingId) {
        popular(model, emailUsuario, form, editingId, mesSelecionado);
    }

    public void popular(Model model, String emailUsuario, TransacaoForm form, Long editingId, YearMonth mesSelecionado) {
        YearMonth mes = mesSelecionado != null ? mesSelecionado : YearMonth.now();
        Usuario usuario = usuarioService.buscarPorEmail(emailUsuario);
        salarioService.lancarSalarioDoMes(usuario);

        DashboardDados dashboard = transacaoService.carregarDashboard(emailUsuario, mes);
        Salario salario = salarioService.buscarPorUsuario(usuario);

        List<YearMonth> mesesComTransacoes = transacaoService.listarMesesComTransacoes(emailUsuario);
        List<MesDisponivel> mesesDisponiveis = montarMesesDisponiveis(mesesComTransacoes, mes);
        Map<Integer, List<MesDisponivel>> mesesPorAno = agruparPorAno(mesesDisponiveis);
        List<Integer> anosDisponiveis = mesesPorAno.keySet().stream().sorted((a, b) -> b - a).toList();
        List<MesDisponivel> mesesDoAnoSelecionado = mesesPorAno.getOrDefault(mes.getYear(), List.of());

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
        model.addAttribute("transacoes", transacaoService.listarTransacoesDoMes(emailUsuario, mes));
        model.addAttribute("form", form);
        model.addAttribute("editingId", editingId);
        model.addAttribute("modoEdicao", editingId != null);
        model.addAttribute("emailUsuario", emailUsuario);
        model.addAttribute("tipos", TipoTransacao.values());
        model.addAttribute("categorias", filtrarCategoriasDoFormulario());
        model.addAttribute("salario", salario);
        model.addAttribute("diasDoMes", IntStream.rangeClosed(1, 31).boxed().toList());
        model.addAttribute("mesSelecionado", mes);
        model.addAttribute("mesesDisponiveis", mesesDisponiveis);
        model.addAttribute("mesesPorAno", mesesPorAno);
        model.addAttribute("anosDisponiveis", anosDisponiveis);
        model.addAttribute("mesesDoAnoSelecionado", mesesDoAnoSelecionado);
    }

    public static List<MesDisponivel> montarMesesDisponiveis(List<YearMonth> mesesComTransacoes, YearMonth mesSelecionado) {
        Set<YearMonth> meses = new LinkedHashSet<>(mesesComTransacoes);
        meses.add(mesSelecionado);

        return meses.stream()
                .sorted()
                .map(mes -> new MesDisponivel(
                        mes.getYear(),
                        mes.getMonthValue(),
                        mes.getMonth().getDisplayName(TextStyle.SHORT, new Locale("pt", "BR"))
                ))
                .toList();
    }

    public static Map<Integer, List<MesDisponivel>> agruparPorAno(List<MesDisponivel> mesesDisponiveis) {
        Map<Integer, List<MesDisponivel>> porAno = new LinkedHashMap<>();

        for (MesDisponivel mes : mesesDisponiveis) {
            porAno.computeIfAbsent(mes.ano(), ano -> new ArrayList<>()).add(mes);
        }

        return porAno;
    }

    private List<CategoriaTransacao> filtrarCategoriasDoFormulario() {
        return Arrays.stream(CategoriaTransacao.values())
                .filter(categoria -> categoria != CategoriaTransacao.SALARIO)
                .filter(categoria -> categoria != CategoriaTransacao.COMISSAO)
                .toList();
    }
}