package com.mk.gestor_financeiro.repository;

import com.mk.gestor_financeiro.model.TipoTransacao;
import com.mk.gestor_financeiro.model.Transacao;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TransacaoRepository extends JpaRepository<Transacao, Long> {

    List<Transacao> findByUsuarioIdAndDataBetweenOrderByDataDescIdDesc(
            Long usuarioId,
            LocalDate inicio,
            LocalDate fim
    );

    List<Transacao> findByUsuarioIdAndDataBetweenOrderByDataAscIdAsc(
            Long usuarioId,
            LocalDate inicio,
            LocalDate fim
    );

    Optional<Transacao> findByIdAndUsuarioId(Long id, Long usuarioId);

    @Query("""
            select sum(t.valor)
            from Transacao t
            where t.usuario.id = :usuarioId
              and t.tipo = :tipo
            """)
    Optional<BigDecimal> somarValorPorTipo(
            @Param("usuarioId") Long usuarioId,
            @Param("tipo") TipoTransacao tipo
    );

    @Query("""
            select sum(t.valor)
            from Transacao t
            where t.usuario.id = :usuarioId
              and t.tipo = :tipo
              and t.data between :inicio and :fim
            """)
    Optional<BigDecimal> somarValorPorTipoNoPeriodo(
            @Param("usuarioId") Long usuarioId,
            @Param("tipo") TipoTransacao tipo,
            @Param("inicio") LocalDate inicio,
            @Param("fim") LocalDate fim
    );
}
