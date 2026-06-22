package com.mk.gestor_financeiro.service;

import com.mk.gestor_financeiro.dto.DashboardResumo;
import com.mk.gestor_financeiro.dto.CategoriaResumo;
import com.mk.gestor_financeiro.dto.DashboardDados;
import com.mk.gestor_financeiro.dto.EvolucaoMensal;
import com.mk.gestor_financeiro.dto.InsightFinanceiro;
import com.mk.gestor_financeiro.dto.MetaFinanceira;
import com.mk.gestor_financeiro.dto.TransacaoForm;
import com.mk.gestor_financeiro.model.CategoriaTransacao;
import com.mk.gestor_financeiro.model.TipoTransacao;
import com.mk.gestor_financeiro.model.Transacao;
import com.mk.gestor_financeiro.model.Usuario;
import com.mk.gestor_financeiro.model.Reserva;
import com.mk.gestor_financeiro.repository.TransacaoRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.format.TextStyle;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TransacaoService {

    private static final BigDecimal ZERO = BigDecimal.ZERO;

    private final TransacaoRepository transacaoRepository;
    private final UsuarioService usuarioService;
    private final ReservaService reservaService;

    @Transactional(readOnly = true)
    public DashboardDados carregarDashboard(String emailUsuario) {
        Usuario usuario = usuarioService.buscarPorEmail(emailUsuario);
        DashboardResumo resumo = calcularResumoDoMes(emailUsuario);
        List<Transacao> transacoesDoMes = listarTransacoesDoMes(emailUsuario);

        return new DashboardDados(
                resumo,
                calcularCategorias(transacoesDoMes, resumo.receitasMes()),
                calcularEvolucao(emailUsuario),
                calcularMeta(usuario),
                gerarInsight(resumo, transacoesDoMes)
        );
    }

    @Transactional(readOnly = true)
    public DashboardResumo calcularResumoDoMes(String emailUsuario) {
        Usuario usuario = usuarioService.buscarPorEmail(emailUsuario);
        YearMonth mesAtual = YearMonth.now();
        LocalDate inicioMes = mesAtual.atDay(1);
        LocalDate fimMes = mesAtual.atEndOfMonth();

        BigDecimal receitasTotais = somar(usuario.getId(), TipoTransacao.RECEITA);
        BigDecimal despesasTotais = somar(usuario.getId(), TipoTransacao.DESPESA);
        BigDecimal receitasMes = somarNoPeriodo(usuario.getId(), TipoTransacao.RECEITA, inicioMes, fimMes);
        BigDecimal despesasMes = somarNoPeriodo(usuario.getId(), TipoTransacao.DESPESA, inicioMes, fimMes);

        return new DashboardResumo(
                receitasTotais.subtract(despesasTotais),
                receitasMes,
                despesasMes,
                inicioMes,
                fimMes
        );
    }

    @Transactional(readOnly = true)
    public List<Transacao> listarTransacoesDoMes(String emailUsuario) {
        Usuario usuario = usuarioService.buscarPorEmail(emailUsuario);
        YearMonth mesAtual = YearMonth.now();

        return transacaoRepository.findByUsuarioIdAndDataBetweenOrderByDataDescIdDesc(
                usuario.getId(),
                mesAtual.atDay(1),
                mesAtual.atEndOfMonth()
        );
    }

    @Transactional(readOnly = true)
    public List<EvolucaoMensal> calcularEvolucao(String emailUsuario) {
        Usuario usuario = usuarioService.buscarPorEmail(emailUsuario);
        YearMonth mesInicial = YearMonth.now().minusMonths(5);
        YearMonth mesFinal = YearMonth.now();
        LocalDate inicio = mesInicial.atDay(1);
        LocalDate fim = mesFinal.atEndOfMonth();

        List<Transacao> transacoes = transacaoRepository.findByUsuarioIdAndDataBetweenOrderByDataAscIdAsc(
                usuario.getId(),
                inicio,
                fim
        );

        Map<YearMonth, BigDecimal> receitas = new LinkedHashMap<>();
        Map<YearMonth, BigDecimal> despesas = new LinkedHashMap<>();

        for (int indice = 0; indice < 6; indice++) {
            YearMonth mes = mesInicial.plusMonths(indice);
            receitas.put(mes, ZERO);
            despesas.put(mes, ZERO);
        }

        for (Transacao transacao : transacoes) {
            YearMonth mes = YearMonth.from(transacao.getData());
            Map<YearMonth, BigDecimal> destino = transacao.getTipo() == TipoTransacao.RECEITA ? receitas : despesas;
            destino.put(mes, destino.get(mes).add(transacao.getValor()));
        }

        BigDecimal maiorValor = receitas.values().stream()
                .max(BigDecimal::compareTo)
                .orElse(ZERO)
                .max(despesas.values().stream().max(BigDecimal::compareTo).orElse(ZERO));

        List<EvolucaoMensal> evolucao = new ArrayList<>();

        for (YearMonth mes : receitas.keySet()) {
            BigDecimal receita = receitas.get(mes);
            BigDecimal despesa = despesas.get(mes);

            evolucao.add(new EvolucaoMensal(
                    mes.getMonth().getDisplayName(TextStyle.SHORT, Locale.forLanguageTag("pt-BR")).replace(".", ""),
                    receita,
                    despesa,
                    receita.subtract(despesa),
                    calcularAlturaBarra(receita, maiorValor),
                    calcularAlturaBarra(despesa, maiorValor)
            ));
        }

        return evolucao;
    }


    @Transactional(readOnly = true)
    public Transacao buscarParaEdicao(String emailUsuario, Long transacaoId) {
        Usuario usuario = usuarioService.buscarPorEmail(emailUsuario);

        return transacaoRepository.findByIdAndUsuarioId(transacaoId, usuario.getId())
                .orElseThrow(() -> new IllegalArgumentException("Transacao nao encontrada."));
    }

    @Transactional
    public void criar(String emailUsuario, TransacaoForm form) {
        Usuario usuario = usuarioService.buscarPorEmail(emailUsuario);
        Transacao transacao = new Transacao();

        aplicarDados(transacao, form);
        transacao.setUsuario(usuario);

        transacaoRepository.save(transacao);
    }

    @Transactional
    public void atualizar(String emailUsuario, Long transacaoId, TransacaoForm form) {
        Transacao transacao = buscarParaEdicao(emailUsuario, transacaoId);
        if (transacao.getCategoria() == CategoriaTransacao.INVESTIMENTOS) {
            throw new IllegalArgumentException("Nao e possivel editar movimentacoes da reserva.");
        }
        aplicarDados(transacao, form);
    }

    @Transactional
    public void excluir(String emailUsuario, Long transacaoId) {
        Transacao transacao = buscarParaEdicao(emailUsuario, transacaoId);
        if (transacao.getCategoria() == CategoriaTransacao.INVESTIMENTOS) {
            throw new IllegalArgumentException("Nao e possivel excluir movimentacoes da reserva.");
        }
        transacaoRepository.delete(transacao);
    }

    private void aplicarDados(Transacao transacao, TransacaoForm form) {
        if (form.getValor() == null || form.getValor().compareTo(ZERO) <= 0) {
            throw new IllegalArgumentException("O valor precisa ser maior que zero.");
        }

        transacao.setDescricao(form.getDescricao().trim());
        transacao.setValor(form.getValor());
        transacao.setData(form.getData());
        transacao.setTipo(form.getTipo());
        transacao.setCategoria(form.getCategoria());
    }

    private BigDecimal somar(Long usuarioId, TipoTransacao tipo) {
        return transacaoRepository.somarValorPorTipo(usuarioId, tipo).orElse(ZERO);
    }

    private BigDecimal somarNoPeriodo(Long usuarioId, TipoTransacao tipo, LocalDate inicio, LocalDate fim) {
        return transacaoRepository.somarValorPorTipoNoPeriodo(usuarioId, tipo, inicio, fim).orElse(ZERO);
    }

    private List<CategoriaResumo> calcularCategorias(List<Transacao> transacoesDoMes, BigDecimal receitasMes) {
        Map<CategoriaTransacao, BigDecimal> despesasPorCategoria = new LinkedHashMap<>();

        for (CategoriaTransacao categoria : CategoriaTransacao.values()) {
            if (categoria != CategoriaTransacao.INVESTIMENTOS) {
                despesasPorCategoria.put(categoria, ZERO);
            }
        }

        for (Transacao transacao : transacoesDoMes) {
            if (transacao.getTipo() == TipoTransacao.DESPESA && transacao.getCategoria() != CategoriaTransacao.INVESTIMENTOS) {
                despesasPorCategoria.merge(transacao.getCategoria(), transacao.getValor(), BigDecimal::add);
            }
        }

        return despesasPorCategoria.entrySet().stream()
                .sorted(Map.Entry.<CategoriaTransacao, BigDecimal>comparingByValue(Comparator.reverseOrder()))
                .map(item -> new CategoriaResumo(
                        item.getKey().getDescricao(),
                        item.getValue(),
                        calcularPercentual(item.getValue(), receitasMes)
                ))
                .toList();
    }

    private MetaFinanceira calcularMeta(Usuario usuario) {
        Reserva reserva = reservaService.buscarPorUsuario(usuario);

        BigDecimal objetivo = reserva != null ? reserva.getObjetivo() : BigDecimal.valueOf(1000);
        BigDecimal acumulado = reserva != null ? reserva.getAcumulado() : ZERO;
        BigDecimal restante = objetivo.subtract(acumulado).max(ZERO);

        return new MetaFinanceira(
                "Reserva de seguranca",
                objetivo,
                acumulado,
                calcularPercentual(acumulado, objetivo),
                restante
        );
    }

    private InsightFinanceiro gerarInsight(DashboardResumo resumo, List<Transacao> transacoesDoMes) {
        if (resumo.receitasMes().compareTo(ZERO) == 0 && resumo.despesasMes().compareTo(ZERO) == 0) {
            return new InsightFinanceiro(
                    "Comece pelo basico",
                    "Cadastre receitas e despesas para receber analises mais precisas.",
                    "neutro"
            );
        }

        BigDecimal margem = resumo.receitasMes().subtract(resumo.despesasMes());

        if (margem.compareTo(ZERO) < 0) {
            return new InsightFinanceiro(
                    "Atencao ao caixa",
                    "As despesas do mes passaram das receitas. Revise os maiores gastos antes de assumir novos compromissos.",
                    "alerta"
            );
        }

        return transacoesDoMes.stream()
                .filter(transacao -> transacao.getTipo() == TipoTransacao.DESPESA)
                .max(Comparator.comparing(Transacao::getValor))
                .map(transacao -> new InsightFinanceiro(
                        "Oportunidade de economia",
                        "Seu maior gasto recente foi em " + transacao.getCategoria().getDescricao()
                                + ". Uma reducao de 10% nessa categoria ja melhora sua sobra mensal.",
                        "positivo"
                ))
                .orElse(new InsightFinanceiro(
                        "Bom ritmo",
                        "Voce fechou o mes com sobra. Direcione parte dela para sua reserva.",
                        "positivo"
                ));
    }

    private BigDecimal calcularPercentual(BigDecimal valor, BigDecimal total) {
        if (total.compareTo(ZERO) == 0) {
            return ZERO;
        }

        return valor.multiply(BigDecimal.valueOf(100))
                .divide(total, 1, RoundingMode.DOWN)
                .min(BigDecimal.valueOf(100));
    }

    private int calcularAlturaBarra(BigDecimal valor, BigDecimal maiorValor) {
        int minimoVisual = valor.compareTo(ZERO) > 0 ? 10 : 0;

        if (maiorValor.compareTo(ZERO) == 0) {
            return minimoVisual;
        }

        int percentual = valor.multiply(BigDecimal.valueOf(100))
                .divide(maiorValor, 0, RoundingMode.HALF_UP)
                .intValue();

        return Math.min(100, Math.max(minimoVisual, arredondarParaDez(percentual)));
    }

    private int arredondarParaDez(int percentual) {
        return ((percentual + 5) / 10) * 10;
    }
}