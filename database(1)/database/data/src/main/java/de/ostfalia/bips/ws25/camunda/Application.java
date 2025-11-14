package de.ostfalia.bips.ws25.camunda;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "de.ostfalia.bips.ws25.camunda")
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
