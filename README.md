# Gestor Financeiro

Um MVP (Mínimo Produto Viável) de um gestor financeiro pessoal, construído com um foco em simplicidade, segurança e uma stack de tecnologia moderna. O projeto demonstra como criar uma aplicação web reativa e eficiente com renderização no lado do servidor.

**Acesse a versão online:** [https://gestor-financeiro-2lp9.onrender.com/login](https://gestor-financeiro-2lp9.onrender.com/login)

## ✨ Principais Funcionalidades

*   **Dashboard Interativo:** Resumo mensal de receitas, despesas e saldo atual.
*   **Gerenciamento de Transações:** CRUD completo para receitas e despesas.
*   **Metas Financeiras:** Acompanhe o progresso da sua reserva de segurança.
*   **Análise de Gastos:** Gráficos e listas que detalham os gastos por categoria.
*   **Autenticação e Perfil:** Sistema de login seguro e gerenciamento de perfil de usuário.
*   **Assistente Virtual (Client-side):** Um bot simples para responder a perguntas sobre suas finanças com base nos dados da página.
*   **Tema Claro e Escuro:** Interface adaptável à preferência do usuário.

## 🚀 Tecnologias Utilizadas

Este projeto integra tecnologias modernas para criar uma experiência de usuário fluida, mantendo a lógica principal no backend.

### Backend
- **Java 21 & Spring Boot 3:** Plataforma principal para a construção da aplicação, oferecendo um ecossistema robusto com injeção de dependências, segurança e acesso a dados.
- **Spring Data JPA:** Simplifica a camada de persistência de dados, interagindo com o banco de dados através de repositórios.
- **Spring Security:** Gerencia a autenticação e autorização, protegendo as rotas e garantindo que os usuários acessem apenas seus próprios dados.
- **Flyway:** Ferramenta para versionamento e migração de banco de dados, garantindo que o schema evolua de forma consistente em todos os ambientes.

### Frontend
- **Thymeleaf:** Motor de templates do lado do servidor que gera o HTML dinamicamente, permitindo a construção de interfaces ricas com dados do backend.
- **HTMX:** Biblioteca que aprimora a interatividade da aplicação, permitindo realizar requisições AJAX e atualizar partes da página diretamente do HTML, sem a necessidade de escrever JavaScript complexo. É a base para a reatividade da interface.
- **Tailwind CSS:** Framework de CSS utility-first para a estilização rápida e customizada da interface.
- **JavaScript (Vanilla):** Utilizado para pequenas melhorias de experiência do usuário, como a troca de tema e a inicialização de componentes.

### Banco de Dados
- **PostgreSQL:** Banco de dados relacional robusto, utilizado para o ambiente de produção.
- **H2 Database:** Banco de dados em memória para desenvolvimento e testes locais, facilitando a inicialização do projeto sem dependências externas.

### Build & Deploy
- **Maven:** Gerenciador de dependências e build do projeto Java.
- **Docker:** A aplicação pode ser containerizada para um deploy simplificado e consistente, conforme definido no `Dockerfile`.

## 💻 Rodando o Projeto

### Localmente (com banco H2 em memória)

O perfil `local` é ativado por padrão ao rodar o comando abaixo. Ele utiliza um banco de dados H2 em memória e aplica as migrações do Flyway automaticamente. Os dados serão perdidos ao encerrar a aplicação.

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
