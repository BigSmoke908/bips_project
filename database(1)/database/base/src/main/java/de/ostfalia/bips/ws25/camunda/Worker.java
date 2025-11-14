package de.ostfalia.bips.ws25.camunda;

import java.util.Map;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import io.camunda.client.annotation.JobWorker;
import io.camunda.client.annotation.Variable;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@Component
public class Worker {
    private static final Logger LOGGER = LoggerFactory.getLogger(Worker.class);

    @JobWorker(type = "hello-world")
    public Map<String, Object> hello(@Variable(name = "username") String username) {
        LOGGER.info("Hello, World: {}", username);
        return Map.of("hello-world", true);
    }

    @JobWorker(type = "login")
    public Map<String, Object> login(@Variable(name = "username") String username, @Variable(name = "password") String password) throws Exception {
        final Connection connection = DriverManager.getConnection(
            "jdbc:mysql://localhost:3306/bips_ws25?useSSL=false&allowPublicKeyRetrieval=true",
            "root",
            "Moto2014"
        );

        final PreparedStatement statement = connection.prepareStatement("SELECT * FROM user WHERE user.username = ?");
        statement.setString(1, username);
        final ResultSet result = statement.executeQuery();

        boolean login = false;

        while(result.next() && !login) {
            final String hash = result.getString("password");
            login = Objects.equals(hash(password), hash);
        }

        result.close();
        statement.close();
        connection.close();

        return Map.of("login", login);
    }

    private String hash(String password) {
        return password;
    }

    @JobWorker(type = "ablehnungBetreuerProjektSeminarArbeitMail")
    public Map<String, Object> ablehnungMail(@Variable(name = "username") String username) {
        LOGGER.info("Die Arbeit wurde vom Betreuer abgelehnt", username);
        return Map.of("mail-verschickt", true);
    }

    @JobWorker(type = "annahmeBetreuerProjektSeminarArbeitMail")
    public Map<String, Object> annahmeMail(@Variable(name = "username") String username) {
        LOGGER.info("Die Arbeit wurde vom Betreuer angenommen", username);
        return Map.of("mail-verschickt", true);
    }

    @JobWorker(type = "saveProjektSeminarArbeitAntrageToDatabase")
    public Map<String, Object> saveProjektSeminarArbeitAntrageToDatabase(@Variable(name = "username") String username) {
        LOGGER.info("Saving to Database...", username); //TODO
        return Map.of("database-save", true);
    }

    @JobWorker(type = "ablehnungBetreuerAbschlussarbeitMail")
    public Map<String, Object> ablehnungMailAbschlussarbeit(@Variable(name = "username") String username) {
        LOGGER.info("Die Arbeit wurde vom Betreuer oder vom Zweitbetreuer abgelehnt", username);
        return Map.of("mail-verschickt", true);
    }

    @JobWorker(type = "ablehnungPAAbschlussarbeitMail")
    public Map<String, Object> ablehnungMailAbschlussarbeitPA(@Variable(name = "username") String username) {
        LOGGER.info("Die Arbeit wurde vom PA abgelehnt", username);
        return Map.of("mail-verschickt", true);
    }

    @JobWorker(type = "ablehnungPAAbschlussarbeitMail")
    public Map<String, Object> ablehnungMailAbschlussarbeitSBB(@Variable(name = "username") String username) {
        LOGGER.info("Die Arbeit wurde vom SBB abgelehnt", username);
        return Map.of("mail-verschickt", true);
    }

    @JobWorker(type = "informStudentAboutDeadlineOfAbschlussarbeit")
    public Map<String, Object> informStudentAboutDeadlineOfAbschlussarbeit(@Variable(name = "username") String username) {
        LOGGER.info("Deadlien: ", username); //TODO
        return Map.of("mail-verschickt", true);
    }

    @JobWorker(type = "saveProjektSeminarArbeitAntrageToDatabase")
    public Map<String, Object> saveProjektAbschlussarbeitAntragToDatabase(@Variable(name = "username") String username) {
        LOGGER.info("Saving to Database...", username); //TODO
        return Map.of("database-save", true);
    }

    @JobWorker(type = "getDeadline")
    public Map<String, Object> getDeadline(@Variable(name = "username") String username) {
        LOGGER.info("getting Deadlin", username); //TODO
        return Map.of("deadline-determined", true);
    }

}
