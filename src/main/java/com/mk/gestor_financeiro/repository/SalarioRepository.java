package com.mk.gestor_financeiro.repository;

import com.mk.gestor_financeiro.model.Salario;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SalarioRepository extends JpaRepository<Salario, Long> {
    Optional<Salario> findByUsuarioId(Long usuarioId);

    boolean existsByUsuarioId(Long usuarioId);
}