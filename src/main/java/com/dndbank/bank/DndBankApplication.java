package com.dndbank.bank;

import com.dndbank.bank.config.DatabaseConnectionChecker;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DndBankApplication {

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(DndBankApplication.class);
        app.addListeners(new DatabaseConnectionChecker());
        app.run(args);
    }
}
