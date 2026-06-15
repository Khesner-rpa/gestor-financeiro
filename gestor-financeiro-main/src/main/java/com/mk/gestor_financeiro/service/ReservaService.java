package com.mk.gestor_financeiro.service;

import com.mk.gestor_financeiro.model.Reserva;
import com.mk.gestor_financeiro.model.Usuario;
import com.mk.gestor_financeiro.model.Transacao;
import com.mk.gestor_financeiro.model.TipoTransacao;
import com.mk.gestor_financeiro.model.CategoriaTransacao;
import com.mk.gestor_financeiro.repository.ReservaRepository;
import com.mk.gestor_financeiro.repository.TransacaoRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReservaService {

    private final ReservaRepository reservaRepository;
    private final TransacaoRepository transacaoRepository;

    @Transactional(readOnly = true)
    public Reserva buscarPorUsuario(Usuario usuario) {
        return reservaRepository.findByUsuarioId(usuario.getId()).orElse(null);
    }

    @Transactional
    public void atualizarObjetivo(Usuario usuario, BigDecimal objetivo) {
        if (objetivo == null || objetivo.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Informe uma meta maior que zero.");
        }

        Reserva reserva = reservaRepository.findByUsuarioId(usuario.getId())
                .orElseGet(() -> criarReservaInicial(usuario));

        reserva.setObjetivo(objetivo);
        reservaRepository.save(reserva);
    }

    @Transactional
    public void depositar(Usuario usuario, BigDecimal valor, BigDecimal saldoAtual) {
        if (valor == null || valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Informe um valor maior que zero.");
        }
        if (saldoAtual.compareTo(valor) < 0) {
            throw new IllegalArgumentException("Voce nao tem saldo suficiente na conta principal.");
        }
        Reserva reserva = reservaRepository.findByUsuarioId(usuario.getId())
                .orElseGet(() -> criarReservaInicial(usuario));
        
        reserva.setAcumulado(reserva.getAcumulado().add(valor));
        reservaRepository.save(reserva);

        Transacao transacao = new Transacao();
        transacao.setUsuario(usuario);
        transacao.setDescricao("Guardado na reserva");
        transacao.setValor(valor);
        transacao.setData(LocalDate.now());
        transacao.setTipo(TipoTransacao.DESPESA);
        transacao.setCategoria(CategoriaTransacao.INVESTIMENTOS);
        transacaoRepository.save(transacao);
    }

    @Transactional
    public void resgatar(Usuario usuario, BigDecimal valor) {
        if (valor == null || valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Informe um valor maior que zero.");
        }
        Reserva reserva = reservaRepository.findByUsuarioId(usuario.getId()).orElse(null);
        if (reserva == null || reserva.getAcumulado().compareTo(valor) < 0) {
            throw new IllegalArgumentException("Voce nao tem esse valor disponivel na reserva.");
        }
        reserva.setAcumulado(reserva.getAcumulado().subtract(valor));
        reservaRepository.save(reserva);

        Transacao transacao = new Transacao();
        transacao.setUsuario(usuario);
        transacao.setDescricao("Resgate da reserva");
        transacao.setValor(valor);
        transacao.setData(LocalDate.now());
        transacao.setTipo(TipoTransacao.RECEITA);
        transacao.setCategoria(CategoriaTransacao.INVESTIMENTOS);
        transacaoRepository.save(transacao);
    }

    private Reserva criarReservaInicial(Usuario usuario) {
        Reserva nova = new Reserva();
        nova.setUsuario(usuario);
        nova.setObjetivo(BigDecimal.valueOf(1000.00));
        nova.setAcumulado(BigDecimal.ZERO);
        return nova;
    }
}
