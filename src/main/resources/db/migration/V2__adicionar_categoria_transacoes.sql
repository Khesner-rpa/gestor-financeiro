<<<<<<< HEAD
ALTER TABLE transacoes ADD COLUMN categoria VARCHAR(40);

UPDATE transacoes
SET categoria = CASE
    WHEN tipo = 'RECEITA' THEN 'SALARIO'
    ELSE 'OUTROS'
END
WHERE categoria IS NULL;

ALTER TABLE transacoes ALTER COLUMN categoria SET NOT NULL;
=======
ALTER TABLE transacoes ADD COLUMN categoria VARCHAR(40);

UPDATE transacoes
SET categoria = CASE
    WHEN tipo = 'RECEITA' THEN 'SALARIO'
    ELSE 'OUTROS'
END
WHERE categoria IS NULL;

ALTER TABLE transacoes ALTER COLUMN categoria SET NOT NULL;
>>>>>>> 6be877a264aef21474bf1ccbbf9c660e2ac5dcce
