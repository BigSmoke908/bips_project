package de.ostfalia.bips.ws25.camunda.database.repository;

import de.ostfalia.bips.ws25.camunda.database.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer>, JpaSpecificationExecutor<User> {
    @Query("SELECT u from User u " +
            "WHERE UPPER(u.username) = UPPER(:username)")
    Optional<User> findByUsername(@Param("username") String username);
}