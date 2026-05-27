package com.mk.gestor_financeiro.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class PerfilForm {

    @NotBlank(message = "Informe seu nome.")
    @Size(max = 120, message = "Use no maximo 120 caracteres.")
    private String nome;

    public static PerfilForm comNome(String nome) {
        PerfilForm form = new PerfilForm();
        form.setNome(nome);
        return form;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }
}
