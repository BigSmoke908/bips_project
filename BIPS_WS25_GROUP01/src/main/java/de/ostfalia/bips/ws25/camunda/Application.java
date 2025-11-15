package de.ostfalia.bips.ws25.camunda;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {
    public static void main(String[] args) throws Exception  {
        Class.forName("com.mysql.jdbc.Driver");
        SpringApplication.run(Application.class, args);
    }
}
