<<<<<<< HEAD
package com.mk.gestor_financeiro.repository;

import com.mk.gestor_financeiro.model.Usuario;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCase(String email);
}
=======
package com.mk.gestor_financeiro.repository;

import com.mk.gestor_financeiro.model.Usuario;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCase(String email);
}
>>>>>>> 6be877a264aef21474bf1ccbbf9c660e2ac5dcce
