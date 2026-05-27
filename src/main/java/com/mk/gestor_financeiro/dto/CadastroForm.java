package com.mk.gestor_financeiro.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CadastroForm {

    @NotBlank(message = "Informe seu nome.")
    private String nome;

    @Email(message = "Informe um e-mail valido.")
    @NotBlank(message = "Informe seu e-mail.")
    private String email;

    @Size(min = 6, message = "Use pelo menos 6 caracteres.")
    @NotBlank(message = "Informe uma senha.")
    private String senha;

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }
}
