package com.mk.gestor_financeiro.repository;

import com.mk.gestor_financeiro.model.Reserva;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservaRepository extends JpaRepository<Reserva, Long> {
    Optional<Reserva> findByUsuarioId(Long usuarioId);
}