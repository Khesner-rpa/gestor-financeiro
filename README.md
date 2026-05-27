# Gestor Financeiro

MVP de financas pessoais com Spring Boot, Thymeleaf, Tailwind CSS e HTMX.

## Rodar localmente

```powershell
.\mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=local
```

O perfil `local` usa H2 em memoria e aplica as migrations do Flyway automaticamente.

## Banco PostgreSQL

Configure as variaveis abaixo para usar PostgreSQL:

```env
DATABASE_URL=jdbc:postgresql://host:5432/gestor_financeiro
DATABASE_USERNAME=usuario
DATABASE_PASSWORD=senha
PORT=8080
```

## Organizacao

- `model`: entidades e enums do dominio.
- `repository`: acesso a dados com Spring Data JPA.
- `service`: regras de negocio, validacoes e calculos financeiros.
- `controller`: rotas web e fragments HTMX.
- `dto`: objetos de formulario e resumo da dashboard.
- `templates`: telas Thymeleaf e fragments compartilhados.
- `db/migration`: evolucao do schema via Flyway.
