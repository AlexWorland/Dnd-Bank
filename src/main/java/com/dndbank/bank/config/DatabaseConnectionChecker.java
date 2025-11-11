package com.dndbank.bank.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.Environment;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnectionChecker implements ApplicationListener<ApplicationEnvironmentPreparedEvent> {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseConnectionChecker.class);

    @Override
    public void onApplicationEvent(ApplicationEnvironmentPreparedEvent event) {
        Environment env = event.getEnvironment();
        String url = env.getProperty("spring.datasource.url", "jdbc:postgresql://localhost:5432/dndbank");
        String username = env.getProperty("spring.datasource.username", "dnd_master");
        String password = env.getProperty("spring.datasource.password", "dnd_master");

        logger.info("Checking PostgreSQL connection at: {}", url.replaceAll(":[^:@]+@", ":****@"));
        
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            boolean isValid = connection.isValid(5);
            if (isValid) {
                logger.info("PostgreSQL connection successful");
            } else {
                logger.error("PostgreSQL connection validation failed");
                printExitMessage(url);
                System.exit(1);
            }
        } catch (SQLException e) {
            logger.error("Failed to connect to PostgreSQL: {}", e.getMessage());
            printExitMessage(url);
            System.exit(1);
        }
    }

    private void printExitMessage(String url) {
        System.err.println();
        System.err.println("╔══════════════════════════════════════════════════════════════╗");
        System.err.println("║                    PostgreSQL Not Running                    ║");
        System.err.println("╠══════════════════════════════════════════════════════════════╣");
        System.err.println("║  The application cannot connect to PostgreSQL.               ║");
        System.err.println("║                                                              ║");
        System.err.println("║  To start PostgreSQL, run:                                  ║");
        System.err.println("║    docker compose up -d db                                  ║");
        System.err.println("║                                                              ║");
        System.err.println("║  Or start the full stack:                                    ║");
        System.err.println("║    docker compose up --build                                ║");
        System.err.println("║                                                              ║");
        System.err.println("║  Expected connection: " + String.format("%-35s", url) + " ║");
        System.err.println("╚══════════════════════════════════════════════════════════════╝");
        System.err.println();
    }
}

