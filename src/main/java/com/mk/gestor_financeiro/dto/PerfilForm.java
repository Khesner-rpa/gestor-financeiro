<<<<<<< HEAD
package com.mk.gestor_financeiro.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PerfilForm {

    @NotBlank(message = "Informe seu nome.")
    @Size(max = 120, message = "Use no maximo 120 caracteres.")
    private String nome;

    public static PerfilForm comNome(String nome) {
        PerfilForm form = new PerfilForm();
        form.setNome(nome);
        return form;
    }
}
=======
package com.mk.gestor_financeiro.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PerfilForm {

    @NotBlank(message = "Informe seu nome.")
    @Size(max = 120, message = "Use no maximo 120 caracteres.")
    private String nome;

    public static PerfilForm comNome(String nome) {
        PerfilForm form = new PerfilForm();
        form.setNome(nome);
        return form;
    }
}
>>>>>>> 6be877a264aef21474bf1ccbbf9c660e2ac5dcce
