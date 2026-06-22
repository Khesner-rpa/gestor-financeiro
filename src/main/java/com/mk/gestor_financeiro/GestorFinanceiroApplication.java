package com.mk.gestor_financeiro;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Properties;

@SpringBootApplication
public class GestorFinanceiroApplication {

    // Guarda a escolha apenas na memoria da JVM atual.
    // O DevTools reinicia com o mesmo processo, entao
    // essa variavel sobrevive ao restart sem salvar nada em disco.
    // Quando o usuario faz Ctrl+C e roda novamente, um novo
    // processo e criado e a variavel volta a ser null.
    private static Properties cachedProps = null;

    public static void main(String[] args) {
        if (cachedProps == null) {
            cachedProps = perguntarOndeGuardarDados();
        }

        SpringApplication app = new SpringApplication(GestorFinanceiroApplication.class);
        app.setDefaultProperties(cachedProps);
        app.run(args);
    }

    private static Properties perguntarOndeGuardarDados() {
        Properties props = new Properties();

        System.out.println();
        System.out.println("+--------------------------------------------------+");
        System.out.println("|     GESTOR FINANCEIRO -- INICIALIZACAO           |");
        System.out.println("+--------------------------------------------------+");
        System.out.println();
        System.out.println("Onde deseja guardar os dados?");
        System.out.println("  [1] Memoria (H2 - dados perdidos ao encerrar)");
        System.out.println("  [2] Nuvem   (PostgreSQL - requer banco configurado)");
        System.out.println();
        System.out.print("Digite 1 ou 2: ");
        System.out.flush();

        String escolha = lerLinha();

        if ("2".equals(escolha)) {
            configurarCloud(props);
        } else {
            if (!"1".equals(escolha)) {
                System.out.println();
                System.out.println("[AVISO] Opcao invalida. Usando memoria como padrao.");
            }
            configurarMemoria(props);
        }

        return props;
    }

    private static void configurarMemoria(Properties props) {
        System.out.println();
        System.out.println("[OK] Iniciando com banco em memoria (H2).");
        System.out.println("     Os dados serao perdidos ao encerrar o servidor.");
        System.out.println();

        props.setProperty("spring.datasource.url",
                "jdbc:h2:mem:gestor_financeiro" +
                ";MODE=PostgreSQL;DB_CLOSE_DELAY=-1" +
                ";DATABASE_TO_LOWER=TRUE;DEFAULT_NULL_ORDERING=HIGH");
        props.setProperty("spring.datasource.username", "sa");
        props.setProperty("spring.datasource.password", "");
        props.setProperty("spring.datasource.driver-class-name", "org.h2.Driver");
        props.setProperty("spring.jpa.hibernate.ddl-auto", "validate");
        props.setProperty("spring.flyway.enabled", "true");
        props.setProperty("spring.h2.console.enabled", "true");
        props.setProperty("spring.h2.console.path", "/h2-console");
    }

    private static void configurarCloud(Properties props) {
        System.out.println();

        String urlEnv      = System.getenv("DATABASE_URL");
        String userEnv     = System.getenv("DATABASE_USERNAME");
        String passwordEnv = System.getenv("DATABASE_PASSWORD");

        boolean temCredenciais = urlEnv      != null && !urlEnv.isBlank()
                              && userEnv     != null && !userEnv.isBlank()
                              && passwordEnv != null && !passwordEnv.isBlank();

        if (temCredenciais) {
            System.out.println("[OK] Variaveis de ambiente detectadas.");
            System.out.println("     DATABASE_URL: " + mascararUrl(urlEnv));
            System.out.println();
            aplicarPostgres(props, urlEnv, userEnv, passwordEnv);
            return;
        }

        System.out.println("Nenhuma variavel de ambiente encontrada.");
        System.out.println("Informe os dados de conexao (Enter em branco cancela e usa memoria):");
        System.out.println();

        System.out.print("URL do banco (ex: jdbc:postgresql://host:5432/db): ");
        System.out.flush();
        String url = lerLinha();

        if (url.isBlank()) {
            System.out.println("[AVISO] Nenhuma URL informada. Usando memoria como fallback.");
            configurarMemoria(props);
            return;
        }

        System.out.print("Usuario: ");
        System.out.flush();
        String user = lerLinha();

        if (user.isBlank()) {
            System.out.println("[AVISO] Usuario nao informado. Usando memoria como fallback.");
            configurarMemoria(props);
            return;
        }

        System.out.print("Senha: ");
        System.out.flush();
        String password = lerLinha();

        System.out.println();
        System.out.println("[OK] Conectando ao banco PostgreSQL...");
        System.out.println();

        aplicarPostgres(props, url, user, password);
    }

    private static void aplicarPostgres(Properties props,
                                        String url,
                                        String user,
                                        String password) {
        props.setProperty("spring.datasource.url",      url);
        props.setProperty("spring.datasource.username", user);
        props.setProperty("spring.datasource.password", password);
        props.setProperty("spring.datasource.driver-class-name", "org.postgresql.Driver");
        props.setProperty("spring.jpa.hibernate.ddl-auto", "validate");
        props.setProperty("spring.flyway.baseline-on-migrate", "true");
    }

    private static String lerLinha() {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            String linha = reader.readLine();
            return linha != null ? linha.trim() : "";
        } catch (Exception e) {
            return "";
        }
    }

    private static String mascararUrl(String url) {
        return url.replaceAll(":[^:@/]+@", ":****@");
    }
}