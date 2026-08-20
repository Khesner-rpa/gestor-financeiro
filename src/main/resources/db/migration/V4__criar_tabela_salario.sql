CREATE TABLE salario (
    id BIGSERIAL PRIMARY KEY,
    valor NUMERIC(12, 2) NOT NULL,
    dia_recebimento INTEGER NOT NULL,
    fixo BOOLEAN NOT NULL DEFAULT TRUE,
    usuario_id BIGINT NOT NULL UNIQUE,
    CONSTRAINT fk_salario_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios (id) ON DELETE CASCADE
);