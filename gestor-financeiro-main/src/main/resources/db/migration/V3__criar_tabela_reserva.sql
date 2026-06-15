CREATE TABLE reserva (
    id BIGSERIAL PRIMARY KEY,
    objetivo DECIMAL(19, 2) NOT NULL DEFAULT 1000.00,
    acumulado DECIMAL(19, 2) NOT NULL DEFAULT 0.00,
    usuario_id BIGINT NOT NULL UNIQUE,
    CONSTRAINT fk_reserva_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios (id) ON DELETE CASCADE
);