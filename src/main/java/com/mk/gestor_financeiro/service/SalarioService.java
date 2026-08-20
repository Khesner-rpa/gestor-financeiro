package com.mk.gestor_financeiro.service;

import com.mk.gestor_financeiro.model.CategoriaTransacao;
import com.mk.gestor_financeiro.model.Salario;
import com.mk.gestor_financeiro.model.TipoTransacao;
import com.mk.gestor_financeiro.model.Transacao;
import com.mk.gestor_financeiro.model.Usuario;
import com.mk.gestor_financeiro.repository.SalarioRepository;
import com.mk.gestor_financeiro.repository.TransacaoRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SalarioService {

    private static final BigDecimal ZERO = BigDecimal.ZERO;

    private final SalarioRepository salarioRepository;
    private final TransacaoRepository transacaoRepository;

    @Transactional(readOnly = true)
    public Salario buscarPorUsuario(Usuario usuario) {
        return salarioRepository.findByUsuarioId(usuario.getId()).orElse(null);
    }

    @Transactional
    public void definir(Usuario usuario, BigDecimal valor, Integer diaRecebimento, boolean fixo) {
        if (valor == null || valor.compareTo(ZERO) <= 0) {
            throw new IllegalArgumentException("Informe um valor de salario maior que zero.");
        }
        if (diaRecebimento == null || diaRecebimento < 1 || diaRecebimento > 31) {
            throw new IllegalArgumentException("Escolha um dia de recebimento entre 1 e 31.");
        }

        Salario salario = salarioRepository.findByUsuarioId(usuario.getId())
                .orElseGet(Salario::new);

        salario.setUsuario(usuario);
        salario.setValor(valor);
        salario.setDiaRecebimento(diaRecebimento);
        salario.setFixo(fixo);
        salarioRepository.save(salario);
    }

    @Transactional
    public void adicionarComissao(Usuario usuario, BigDecimal valor) {
        if (valor == null || valor.compareTo(ZERO) <= 0) {
            throw new IllegalArgumentException("Informe um valor de comissao maior que zero.");
        }

        Transacao transacao = new Transacao();
        transacao.setUsuario(usuario);
        transacao.setDescricao("Comissao do mes");
        transacao.setValor(valor);
        transacao.setData(LocalDate.now());
        transacao.setTipo(TipoTransacao.RECEITA);
        transacao.setCategoria(CategoriaTransacao.COMISSAO);
        transacaoRepository.save(transacao);
    }

    @Transactional
    public void lancarSalarioDoMes(Usuario usuario) {
        Salario salario = salarioRepository.findByUsuarioId(usuario.getId()).orElse(null);
        if (salario == null || !salario.isFixo()) {
            return;
        }

        YearMonth mes = YearMonth.now();
        LocalDate inicio = mes.atDay(1);
        LocalDate fim = mes.atEndOfMonth();

        if (transacaoRepository.existeTransacaoNoPeriodo(
                usuario.getId(), CategoriaTransacao.SALARIO, inicio, fim)) {
            return;
        }

        int ultimoDia = fim.getDayOfMonth();
        int dia = Math.min(salario.getDiaRecebimento(), ultimoDia);
        LocalDate dataRecebimento = mes.atDay(dia);

        if (LocalDate.now().isBefore(dataRecebimento)) {
            return;
        }

        Transacao transacao = new Transacao();
        transacao.setUsuario(usuario);
        transacao.setDescricao("Salario fixo");
        transacao.setValor(salario.getValor());
        transacao.setData(dataRecebimento);
        transacao.setTipo(TipoTransacao.RECEITA);
        transacao.setCategoria(CategoriaTransacao.SALARIO);
        transacaoRepository.save(transacao);
    }
}