<<<<<<< HEAD
package com.mk.gestor_financeiro.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import lombok.Data;

@Data
@Entity
public class Reserva {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private BigDecimal objetivo = BigDecimal.valueOf(1000.00);

    @Column(nullable = false)
    private BigDecimal acumulado = BigDecimal.ZERO;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false, unique = true)
    private Usuario usuario;
=======
package com.mk.gestor_financeiro.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import lombok.Data;

@Data
@Entity
public class Reserva {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private BigDecimal objetivo = BigDecimal.valueOf(1000.00);

    @Column(nullable = false)
    private BigDecimal acumulado = BigDecimal.ZERO;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false, unique = true)
    private Usuario usuario;
>>>>>>> 6be877a264aef21474bf1ccbbf9c660e2ac5dcce
}