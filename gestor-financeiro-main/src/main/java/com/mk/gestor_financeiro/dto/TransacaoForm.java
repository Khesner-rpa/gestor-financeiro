package com.mk.gestor_financeiro.dto;

import com.mk.gestor_financeiro.model.CategoriaTransacao;
import com.mk.gestor_financeiro.model.TipoTransacao;
import com.mk.gestor_financeiro.model.Transacao;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDate;

public class TransacaoForm {

    @NotBlank(message = "Descreva a movimentacao.")
    private String descricao;

    @NotNull(message = "Informe o valor.")
    @Positive(message = "O valor precisa ser maior que zero.")
    private BigDecimal valor;

    @NotNull(message = "Informe a data.")
    private LocalDate data;

    @NotNull(message = "Escolha o tipo.")
    private TipoTransacao tipo;

    @NotNull(message = "Escolha a categoria.")
    private CategoriaTransacao categoria;

    public static TransacaoForm novo() {
        TransacaoForm form = new TransacaoForm();
        form.setData(LocalDate.now());
        form.setTipo(TipoTransacao.DESPESA);
        form.setCategoria(CategoriaTransacao.ALIMENTACAO);
        return form;
    }

    public static TransacaoForm de(Transacao transacao) {
        TransacaoForm form = new TransacaoForm();
        form.setDescricao(transacao.getDescricao());
        form.setValor(transacao.getValor());
        form.setData(transacao.getData());
        form.setTipo(transacao.getTipo());
        form.setCategoria(transacao.getCategoria());
        return form;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    public TipoTransacao getTipo() {
        return tipo;
    }

    public void setTipo(TipoTransacao tipo) {
        this.tipo = tipo;
    }

    public CategoriaTransacao getCategoria() {
        return categoria;
    }

    public void setCategoria(CategoriaTransacao categoria) {
        this.categoria = categoria;
    }
}
