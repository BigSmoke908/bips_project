package de.ostfalia.bips.ws25.camunda;

import java.util.HashMap;
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
public class WorkerAnmeldung {
    private static final Logger LOGGER = LoggerFactory.getLogger(WorkerAnmeldung.class);

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

    /**
     * @param betreuer_abschlussarbeit the name of the selected betreuer, if a dozent is selected this is null
     * @param zweitbetreuer the name of the selected zweitbetreuer, if a dozent is selected this is null
     * @param betreuer_abschlussarbeit_select the id of the selected lecturer
     * @param zweitbetreuer_select the id of the selected lecturer (in this case for the second lecturer)
     * @return the assignees for checking wether they are actually supervisors for the project
     */
    @JobWorker(type = "getAssigneeForBetreuerAbschlussarbeit")
    public Map<String, Object> getAssigneeForBetreuerAbschlussarbeit(
                                @Variable(name = "betreuer_abschlussarbeit") String betreuer_abschlussarbeit,
                                @Variable(name = "zweitbetreuer") String zweitbetreuer,
                                @Variable(name = "betreuer_abschlussarbeit_select") String betreuer_abschlussarbeit_select,
                                @Variable(name = "zweitbetreuer_select") String zweitbetreuer_select
                                ) {
        //if the betreuer_abschlussarbeit or zweitbetreuer null, then we need to get the selected lecturer from our database 
        LOGGER.info("Assignees werden bestimmt..."); 
        return Anmeldung.getAssigneeForBetreuerAbschlussarbeit(betreuer_abschlussarbeit_select, zweitbetreuer_select, zweitbetreuer, zweitbetreuer_select);
    }

    @JobWorker(type = "ladeDozentenUndStudiengänge")
    public Map<String, Object> ladeDozenten() {
        LOGGER.info("Dozenten und Studiengänge werden geladen..."); 
        Map<String, Object> mapAll = new HashMap<>();
        Map<String, Object> mapDozenten = Anmeldung.ladeDozenten();
        Map<String, Object> mapCourseOfStudies = Anmeldung.ladeStudiengaenge();
        Map<String, Object> mapSemester = Anmeldung.ladeSemester();
        mapAll.putAll(mapDozenten);
        mapAll.putAll(mapCourseOfStudies);
        mapAll.putAll(mapSemester);
        LOGGER.info("Dozenten und Studiengänge erfolgreich geladen");
        return mapAll;
    }

    @JobWorker(type = "ablehnungBetreuerProjektSeminarArbeitMail")
    public Map<String, Object> ablehnungMail(
                                    @Variable(name = "betreuer") String betreuer,
                                    @Variable(name = "student_name") String student_name, 
                                    @Variable(name = "Email_student") String email, 
                                    @Variable(name = "thema_der_arbeit") String arbietsThema) {
        LOGGER.info("Die Arbeit wurde vom Betreuer abgelehnt");
        String message = "Ihre Arbeit '" + arbietsThema + "' wurde vom Betreuer " + betreuer + " abgelehnt.";
        Utils.simulateEmail(student_name, message, email);
        return Map.of("mail-verschickt", true);
    }

    @JobWorker(type = "annahmeBetreuerProjektSeminarArbeitMail")
    public Map<String, Object> annahmeMail(
                                    @Variable(name = "betreuer") String betreuer,
                                    @Variable(name = "student_name") String student_name, 
                                    @Variable(name = "Email_student") String email, 
                                    @Variable(name = "thema_der_arbeit") String arbietsThema) {
        LOGGER.info("Die Arbeit wurde vom Betreuer angenommen");
        String message = "Ihre Arbeit '" + arbietsThema + "' wurde vom Betreuer " + betreuer + " angenommen.";
        Utils.simulateEmail(student_name, message, email);
        return Map.of("mail-verschickt", true);
    }
    
    @JobWorker(type = "saveProjektSeminarArbeitAntrageToDatabase")
    public Map<String, Object> saveProjektSeminarArbeitAntrageToDatabase(@Variable(name = "student_lastname") String studentLastname,
                                                                            @Variable(name = "student_firstname") String studentFirstname,
                                                                            @Variable(name = "student_title") String studentTitle,
                                                                            @Variable(name = "student_phone") String studentPhone,
                                                                            @Variable(name = "student_mail") String studentMail,
                                                                            @Variable(name = "student_studiengang") String studentStudiengang,
                                                                            @Variable(name = "student_mat_nr") String studenMatNr, //TODO ist im Muster gar nicht verlangt
                                                                            @Variable(name = "semester_arbeit") String semeserterOfArbeit,
                                                                            @Variable(name = "thema_der_arbeit") String themaDerArbeit, //TODO ist das mit thesis gemeint?
                                                                            @Variable(name = "betreuer_vorhanden") String betreuerVorhanden,
                                                                            @Variable(name = "betreuer_extern") String betreuerExtern,
                                                                            @Variable(name = "vorname_betreuer_extern") String vornameBetreuerExtern,
                                                                            @Variable(name = "nachname_betreuer_extern") String nachnameBetreuerExtern,
                                                                            @Variable(name = "title_betreuer_extern") String titleBetreuerExtern,
                                                                            @Variable(name = "email_betreuer_extern") String emailBetreuerExtern,
                                                                            @Variable(name = "phone_betreuer_extern") String phoneBetreuerExetern,
                                                                            @Variable(name = "firma_name") String firmaName,
                                                                            @Variable(name = "firma_adresse") String firmaAdresse,
                                                                            @Variable(name = "firma_plz") String firmaPlz,
                                                                            @Variable(name = "firma_stadt") String firmaStadt,
                                                                            @Variable(name = "betreuer_dozent") String lecturerID, 
                                                                            @Variable(name = "istProjekt") String istProjekt) {
        LOGGER.info("Saving to Database...");
        Anmeldung.saveProjectOrSeminarWorkToDatabase(semeserterOfArbeit, istProjekt, betreuerVorhanden, betreuerExtern, studentStudiengang, lecturerID,
                                                    studentFirstname, studentLastname, studentTitle, studentPhone, studentMail, vornameBetreuerExtern, nachnameBetreuerExtern, 
                                                    titleBetreuerExtern, phoneBetreuerExetern, emailBetreuerExtern, firmaName, firmaAdresse, firmaPlz, firmaStadt);
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
