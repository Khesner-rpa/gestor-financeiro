package com.mk.gestor_financeiro.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.mk.gestor_financeiro.dto.DashboardResumo;
import com.mk.gestor_financeiro.model.CategoriaTransacao;
import com.mk.gestor_financeiro.model.TipoTransacao;
import com.mk.gestor_financeiro.model.Transacao;
import com.mk.gestor_financeiro.model.Usuario;
import com.mk.gestor_financeiro.repository.TransacaoRepository;
import com.mk.gestor_financeiro.repository.UsuarioRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class TransacaoServiceTests {

    @Autowired
    private TransacaoService transacaoService;

    @Autowired
    private TransacaoRepository transacaoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @BeforeEach
    void limparBanco() {
        transacaoRepository.deleteAll();
        usuarioRepository.deleteAll();
    }

    @Test
    void calculaSaldoTotalEResumoDoMes() {
        Usuario usuario = salvarUsuario("ana@exemplo.com");
        LocalDate hoje = LocalDate.now();

        salvarTransacao(usuario, "Salario", "1000.00", TipoTransacao.RECEITA, CategoriaTransacao.SALARIO, hoje);
        salvarTransacao(usuario, "Mercado", "250.00", TipoTransacao.DESPESA, CategoriaTransacao.ALIMENTACAO, hoje);
        salvarTransacao(usuario, "Conta antiga", "100.00", TipoTransacao.DESPESA, CategoriaTransacao.OUTROS, hoje.minusMonths(1));

        DashboardResumo resumo = transacaoService.calcularResumoDoMes(usuario.getEmail());

        assertThat(resumo.saldoAtual()).isEqualByComparingTo("650.00");
        assertThat(resumo.receitasMes()).isEqualByComparingTo("1000.00");
        assertThat(resumo.despesasMes()).isEqualByComparingTo("250.00");
    }

    @Test
    void listaSomenteTransacoesDoUsuarioLogado() {
        Usuario usuario = salvarUsuario("bia@exemplo.com");
        Usuario outroUsuario = salvarUsuario("caio@exemplo.com");
        LocalDate hoje = LocalDate.now();

        salvarTransacao(usuario, "Freelance", "700.00", TipoTransacao.RECEITA, CategoriaTransacao.OUTROS, hoje);
        salvarTransacao(outroUsuario, "Salario externo", "900.00", TipoTransacao.RECEITA, CategoriaTransacao.SALARIO, hoje);

        assertThat(transacaoService.listarTransacoesDoMes(usuario.getEmail()))
                .extracting(Transacao::getDescricao)
                .containsExactly("Freelance");
    }

    private Usuario salvarUsuario(String email) {
        Usuario usuario = new Usuario();
        usuario.setNome("Usuario Teste");
        usuario.setEmail(email);
        usuario.setSenha("senha-criptografada");

        return usuarioRepository.save(usuario);
    }

    private void salvarTransacao(
            Usuario usuario,
            String descricao,
            String valor,
            TipoTransacao tipo,
            CategoriaTransacao categoria,
            LocalDate data
    ) {
        Transacao transacao = new Transacao();
        transacao.setUsuario(usuario);
        transacao.setDescricao(descricao);
        transacao.setValor(new BigDecimal(valor));
        transacao.setTipo(tipo);
        transacao.setCategoria(categoria);
        transacao.setData(data);

        transacaoRepository.save(transacao);
    }
}
