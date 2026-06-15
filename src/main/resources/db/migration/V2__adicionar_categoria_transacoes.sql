ALTER TABLE transacoes ADD COLUMN categoria VARCHAR(40);

UPDATE transacoes
SET categoria = CASE
    WHEN tipo = 'RECEITA' THEN 'SALARIO'
    ELSE 'OUTROS'
END
WHERE categoria IS NULL;

ALTER TABLE transacoes ALTER COLUMN categoria SET NOT NULL;
