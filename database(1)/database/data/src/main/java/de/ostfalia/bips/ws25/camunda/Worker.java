package de.ostfalia.bips.ws25.camunda;

import java.util.Map;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import io.camunda.client.annotation.JobWorker;
import io.camunda.client.annotation.Variable;

import de.ostfalia.bips.ws25.camunda.database.repository.UserRepository;

@Component
public class Worker {
    private static final Logger LOGGER = LoggerFactory.getLogger(Worker.class);

    private final UserRepository userRepository;

    public Worker(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @JobWorker(type = "hello-world")
    public Map<String, Object> hello(@Variable(name = "username") String username) {
        LOGGER.info("Hello, World: {}", username);
        return Map.of("hello-world", true);
    }

    @JobWorker(type = "login")
    public Map<String, Object> login(@Variable(name = "username") String username, @Variable(name = "password") String password) {
        return Map.of("login", userRepository.findByUsername(username)
                .filter(e -> Objects.equals(hash(password), e.getPassword()))
                .isPresent());
    }

    private String hash(String password) {
        return password;
    }
}
