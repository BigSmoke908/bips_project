package de.ostfalia.bips.ws25.camunda.anmeldung_pack;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import de.ostfalia.bips.ws25.camunda.Utils;
import de.ostfalia.bips.ws25.camunda.sql_deserialisation.Dozent;
import io.camunda.client.annotation.JobWorker;
import io.camunda.client.annotation.Variable;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@Component
public class WorkerAnmeldung {
    private static final Logger LOGGER = LoggerFactory.getLogger(WorkerAnmeldung.class);

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
    @JobWorker(type = "getAssignee")
    public Map<String, Object> getAssigneeForBetreuerAbschlussarbeit() {
        //if the betreuer_abschlussarbeit or zweitbetreuer null, then we need to get the selected lecturer from our database 
        //TODO
        LOGGER.info("Assignees werden bestimmt..."); 
        //Anmeldung.getAssigneeForBetreuerAbschlussarbeit(betreuer_abschlussarbeit, zweitbetreuer, betreuer_abschlussarbeit_select, zweitbetreuer_select);
        return Map.of("test", "todo");
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
                                    @Variable(name = "betreuer_extern") String betreuer_extern,
                                    @Variable(name = "vorname_betreuer_extern") String vorname_betreuer_extern,
                                    @Variable(name = "nachname_betreuer_extern") String nachname_betreuer_extern,
                                    @Variable(name = "title_betreuer_extern") String title_betreuer_extern,
                                    @Variable(name = "dozent_id") String dozent_id,
                                    @Variable(name = "student_lastname") String student_lastname, 
                                    @Variable(name = "student_firstname") String student_firstname,
                                    @Variable(name = "student_mail") String email, 
                                    @Variable(name = "thema_der_arbeit") String arbietsThema) {
        LOGGER.info("Die Arbeit wurde vom Betreuer abgelehnt");
        String betreuer = title_betreuer_extern + " " + vorname_betreuer_extern + " " + nachname_betreuer_extern;
        if(betreuer_extern.equals("0")){
            Dozent dozent =  Dozent.getDozentFromId(Integer.parseInt(dozent_id));
            betreuer = dozent.getTitle() + " " + dozent.getFirstname() + " " + dozent.getLastname();
        }
        String student_name = student_firstname + " " +  student_lastname;
        String message = "Ihre Arbeit '" + arbietsThema + "' wurde vom Betreuer " + betreuer + " abgelehnt.";
        Utils.simulateEmail(student_name, message, email);
        return Map.of("mail-verschickt", true);
    }

    @JobWorker(type = "annahmeBetreuerProjektSeminarArbeitMail")
    public Map<String, Object> annahmeMail(
                                    @Variable(name = "betreuer_vorhanden") String betreuer_vorhanden,
                                    @Variable(name = "betreuer_extern") String betreuer_extern,
                                    @Variable(name = "vorname_betreuer_extern") String vorname_betreuer_extern,
                                    @Variable(name = "nachname_betreuer_extern") String nachname_betreuer_extern,
                                    @Variable(name = "title_betreuer_extern") String title_betreuer_extern,
                                    @Variable(name = "dozent_id") String dozent_id,
                                    @Variable(name = "student_lastname") String student_lastname, 
                                    @Variable(name = "student_firstname") String student_firstname,
                                    @Variable(name = "student_mail") String email, 
                                    @Variable(name = "thema_der_arbeit") String arbietsThema) {
        LOGGER.info("Die Arbeit wurde vom Betreuer angenommen");

        String message = "";
        if(betreuer_vorhanden.equals("0")){
            message = "Sie habe ihre Arbeit selbst bestätigt";
        }else{
            String betreuer = title_betreuer_extern + " " + vorname_betreuer_extern + " " + nachname_betreuer_extern;
            if(betreuer_extern.equals("0")){
                Dozent dozent =  Dozent.getDozentFromId(Integer.parseInt(dozent_id));
                betreuer = dozent.concatName();
            }
            message = "Ihre Arbeit '" + arbietsThema + "' wurde vom Betreuer " + betreuer + " angenommen.";
        }

        String student_name = student_firstname + " " + student_lastname;
        
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
                                                                            @Variable(name = "student_mat_nr") String studenMatNr, 
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

        System.out.println("Saving to Database...");
        Anmeldung.saveProjectOrSeminarWorkToDatabase(semeserterOfArbeit, istProjekt, betreuerVorhanden, betreuerExtern, studentStudiengang, lecturerID,
                                                    studentFirstname, studentLastname, studentTitle, studentPhone, studentMail, vornameBetreuerExtern, nachnameBetreuerExtern, 
                                                    titleBetreuerExtern, phoneBetreuerExetern, emailBetreuerExtern, firmaName, firmaAdresse, firmaPlz, firmaStadt, studenMatNr, themaDerArbeit);
        System.out.println("saved to Database");                                                                         
                                                                           
        return Map.of("database-save", true);
    }

    @JobWorker(type = "ablehnungBetreuerAbschlussarbeitMail")
    public Map<String, Object> ablehnungMailAbschlussarbeit(@Variable(name = "thema_der_arbeit") String thema_der_arbeit,
                                                            @Variable(name = "erstbetreuer_extern") String erstbetreuer_extern,
                                                            @Variable(name = "dozent_id") String dozent_id,
                                                            @Variable(name = "vorname_erstbetreuer") String vorname_erstbetreuer,
                                                            @Variable(name = "nachname_erstbetreuer") String nachname_erstbetreuer,
                                                            @Variable(name = "titel_erstbetreuer") String titel_erstbetreuer,
                                                            @Variable(name = "zweitbetreuer_vorhanden") String zweitbetreuer_vorhanden,
                                                            @Variable(name = "zweitbetreuer_extern") String zweitbetreuer_extern,
                                                            @Variable(name = "dozent_id_zweitbetreuer") String dozent_id_zweitbetreuer,
                                                            @Variable(name = "vorname_zweitbetreuer") String vorname_zweitbetreuer,
                                                            @Variable(name = "nachname_zweitbetreuer") String nachname_zweitbetreuer,
                                                            @Variable(name = "titel_zweitbetreuer") String titel_zweitbetreuer,
                                                            @Variable(name = "student_lastname") String student_lastname,
                                                            @Variable(name = "student_firstname") String student_firstname,
                                                            @Variable(name = "student_title") String student_title,
                                                            @Variable(name = "student_mail") String student_mail,
                                                            @Variable(name = "student_mail") String betreuung_angenommen
                                                            ) {
                                                                
        LOGGER.info("Die Arbeit wurde vom Betreuer oder vom Zweitbetreuer abgelehnt");

        String betreuerName = "";
        String zweitbetreuerName = "kein Zweitbetreuer vorhanden";
        String abgehlehntVon = betreuung_angenommen.equals("0") ? "Erstbetreuer":  "Zweitbetreuer";
        if(erstbetreuer_extern.equals("1")){
            betreuerName = titel_erstbetreuer + " " + vorname_erstbetreuer + " " + nachname_erstbetreuer; //erstbetreuer ist immer extern hier
        }else{
            Dozent dozent =  Dozent.getDozentFromId(Integer.parseInt(dozent_id));
            betreuerName = dozent.concatName();
        }   
        if(zweitbetreuer_vorhanden.equals("1")){
            if(zweitbetreuer_extern.equals("1")){
                zweitbetreuerName = titel_zweitbetreuer + " " + vorname_zweitbetreuer + " " + nachname_zweitbetreuer;
            }else{
                zweitbetreuerName = Dozent.getDozentFromId(Integer.parseInt(dozent_id_zweitbetreuer)).concatName();
            }
        }                                                     
        String studentName = student_title + " " + student_firstname + " " + student_lastname;

        StringBuilder messageSb = new StringBuilder();
        messageSb.append("Ihre Arbeit mit dem Thema ").append(thema_der_arbeit).append(" wurde vom ").append(abgehlehntVon).append(" abgelehnt\n");
        messageSb.append("Erstbetreuer: ").append(betreuerName).append("\n");
        messageSb.append("Zweitbetreuer: ").append(zweitbetreuerName).append("\n");
        Utils.simulateEmail(studentName, messageSb.toString(), student_mail);
        return Map.of("mail-verschickt", true);
    }

    @JobWorker(type = "ablehnungPAAbschlussarbeitMail")
    public Map<String, Object> ablehnungMailAbschlussarbeitPA(@Variable(name = "student_lastname") String student_lastname,
                                                            @Variable(name = "student_firstname") String student_firstname,
                                                            @Variable(name = "student_title") String student_title,
                                                            @Variable(name = "student_mail") String student_mail,
                                                            @Variable(name = "student_mail") String betreuung_angenommen) {
        LOGGER.info("Die Arbeit wurde vom PA abgelehnt");
        String message = "Die Arbeit wurde vom PA abgelehnt.";
        //TODO sagen ob vollständigkeit problem oder weitere Angaben
        String studentName = student_title + " " + student_firstname + " " + student_lastname;
        Utils.simulateEmail(studentName, message, student_mail);
        return Map.of("mail-verschickt", true);
    }

    @JobWorker(type = "ablehnungSBBAbschlussarbeitMail")
    public Map<String, Object> ablehnungMailAbschlussarbeitSBB(@Variable(name = "student_lastname") String student_lastname,
                                                            @Variable(name = "student_firstname") String student_firstname,
                                                            @Variable(name = "student_title") String student_title,
                                                            @Variable(name = "student_mail") String student_mail,
                                                            @Variable(name = "student_mail") String betreuung_angenommen) {
        LOGGER.info("Die Arbeit wurde vom PA abgelehnt");
        String message = "Die Arbeit wurde vom PA abgelehnt.";
        //TODO bekommt der Student die Anmerkungen?
        String studentName = student_title + " " + student_firstname + " " + student_lastname;
        Utils.simulateEmail(studentName, message, student_mail);
        return Map.of("mail-verschickt", true);
    }

    @JobWorker(type = "informStudentAboutDeadlineOfAbschlussarbeit")
    public Map<String, Object> informStudentAndBetreuerAboutDeadlineOfAbschlussarbeit(
                                                            @Variable(name = "erstbetreuer_extern") String erstbetreuer_extern,
                                                            @Variable(name = "dozent_id") String dozent_id,
                                                            @Variable(name = "email_erstbetreuer") String email_erstbetreuer,
                                                            @Variable(name = "vorname_erstbetreuer") String vorname_erstbetreuer,
                                                            @Variable(name = "nachname_erstbetreuer") String nachname_erstbetreuer,
                                                            @Variable(name = "titel_erstbetreuer") String titel_erstbetreuer,
                                                            @Variable(name = "email_zweitbetreuer") String email_zweitbetreuer,
                                                            @Variable(name = "zweitbetreuer_vorhanden") String zweitbetreuer_vorhanden,
                                                            @Variable(name = "zweitbetreuer_extern") String zweitbetreuer_extern,
                                                            @Variable(name = "vorname_zweitbetreuer") String vorname_zweitbetreuer,
                                                            @Variable(name = "nachname_zweitbetreuer") String nachname_zweitbetreuer,
                                                            @Variable(name = "titel_zweitbetreuer") String titel_zweitbetreuer,
                                                            @Variable(name = "dozent_id_zweitbetreuer") String dozent_id_zweitbetreuer,
                                                            @Variable(name = "student_lastname") String student_lastname,
                                                            @Variable(name = "student_firstname") String student_firstname,
                                                            @Variable(name = "student_title") String student_title,
                                                            @Variable(name = "student_mail") String student_mail,
                                                            @Variable(name = "thema_der_arbeit") String thema_der_arbeit,
                                                            @Variable(name = "deadline") String deadline) {
        LOGGER.info("informing students and supervisor about the accepted work and the deadline");
        String studentName = student_title + " " + student_firstname + " " + student_lastname;
        String erstbetreuerName = "";
        String zweitbetreuerName = "";

        if(erstbetreuer_extern.equals("1")){
            erstbetreuerName = titel_erstbetreuer + " " +vorname_erstbetreuer + " " + nachname_erstbetreuer;
        }else{
            Dozent dozent = Dozent.getDozentFromId(Integer.parseInt(dozent_id));
            erstbetreuerName = dozent.concatName();
            email_erstbetreuer = dozent.getEmail();
        }

        if(zweitbetreuer_vorhanden.equals("1")){
            if(zweitbetreuer_extern.equals("1")){
                zweitbetreuerName = titel_zweitbetreuer + " " + vorname_zweitbetreuer + " " + nachname_zweitbetreuer;
            }else{
                Dozent dozent = Dozent.getDozentFromId(Integer.parseInt(dozent_id_zweitbetreuer));
                zweitbetreuerName = dozent.concatName();
                email_zweitbetreuer = dozent.getEmail();
            }
        }
        

        String message = "Die Arbeit " + thema_der_arbeit + " wurde vom PA akzeptiert und ist somit offiziell angenommen." + "\n" + 
        "Deadline: " + deadline + "\nStudent: " + studentName + "\nErstbetreuer: " + erstbetreuerName + "\nZweitbetreuer: " + zweitbetreuerName;

        //Mail Student, Erster Betreuer, Zweiter Betreuer
        Utils.simulateEmail(studentName, message, student_mail);
        Utils.simulateEmail(erstbetreuerName, message, email_erstbetreuer);
        if(email_zweitbetreuer != null){
            Utils.simulateEmail(zweitbetreuerName, message, email_zweitbetreuer);
        }    
        return Map.of("mail-verschickt", true);
    }

    @JobWorker(type = "getDeadline")
    public Map<String, Object> getDeadline(@Variable(name = "semester_arbeit") String semester_arbeit) {
        LOGGER.info("getting Deadline");
        String deadline = Anmeldung.getDeadlineFromSemester(semester_arbeit);
        LOGGER.info("got deadline");
        return Map.of("deadline", deadline);
    }

    @JobWorker(type = "saveAbschlussarbeitAntragToDatabase")
    public Map<String, Object> saveAbschlussarbeitAntragToDatabase(@Variable(name = "student_lastname") String studentLastname,
                                                                    @Variable(name = "student_firstname") String studentFirstname,
                                                                    @Variable(name = "student_title") String studentTitle,
                                                                    @Variable(name = "student_phone") String studentPhone,
                                                                    @Variable(name = "student_mail") String studentMail,
                                                                    @Variable(name = "student_studiengang") String studentStudiengang,
                                                                    @Variable(name = "student_mat_nr") String studenMatNr, //TODO ist im Muster gar nicht verlangt
                                                                    @Variable(name = "semester_arbeit") String semester_arbeit,
                                                                    @Variable(name = "erstbetreuer_extern") String erstbetreuer_extern,
                                                                    @Variable(name = "dozent_id") String dozent_id,
                                                                    @Variable(name = "vorname_erstbetreuer") String vorname_erstbetreuer,
                                                                    @Variable(name = "nachname_erstbetreuer") String nachname_erstbetreuer,
                                                                    @Variable(name = "titel_erstbetreuer") String titel_erstbetreuer,
                                                                    @Variable(name = "email_erstbetreuer") String email_erstbetreuer,
                                                                    @Variable(name = "telefon_erstbetreuer") String telefon_erstbetreuer,
                                                                    @Variable(name = "firma_name_erstbetreuer") String firma_name_erstbetreuer,
                                                                    @Variable(name = "firma_adresse_erstbetreuer") String firma_adresse_erstbetreuer,
                                                                    @Variable(name = "firma_plz_erstbetreuer") String firma_plz_erstbetreuer,
                                                                    @Variable(name = "firma_stadt_erstbetreuer") String firma_stadt_erstbetreuer,
                                                                    @Variable(name = "zweitbetreuer_vorhanden") String zweitbetreuer_vorhanden,
                                                                    @Variable(name = "zweitbetreuer_extern") String zweitbetreuer_extern,
                                                                    @Variable(name = "dozent_id_zweitbetreuer") String dozent_id_zweitbetreuer,
                                                                    @Variable(name = "vorname_zweitbetreuer") String vorname_zweitbetreuer,
                                                                    @Variable(name = "nachname_zweitbetreuer") String nachname_zweitbetreuer,
                                                                    @Variable(name = "titel_zweitbetreuer") String titel_zweitbetreuer,
                                                                    @Variable(name = "email_zweitbetreuer") String email_zweitbetreuer,
                                                                    @Variable(name = "telefon_zweitbetreuer") String telefon_zweitbetreuer,
                                                                    @Variable(name = "firma_name_zweitbetreuer") String firma_name_zweitbetreuer,
                                                                    @Variable(name = "firma_adresse_zweitbetreuer") String firma_adresse_zweitbetreuer,
                                                                    @Variable(name = "firma_plz_zweitbetreuer") String firma_plz_zweitbetreuer,
                                                                    @Variable(name = "firma_stadt_zweitbetreuer") String firma_stadt_zweitbetreuer, 
                                                                    @Variable(name = "student_mat_nr") String matrikel_nummer,
                                                                    @Variable(name = "abstract_der_arbeit") String abstract_der_arbeit,
                                                                    @Variable(name = "thema_der_arbeit") String thema_der_arbeit
                                                                    ) {
        LOGGER.info("saving Abschlussarbeit to database...");
        Anmeldung.saveAbschlussarbeitAntragToDatabase(semester_arbeit, zweitbetreuer_vorhanden, erstbetreuer_extern, zweitbetreuer_extern, studentStudiengang,
                                                        dozent_id, dozent_id_zweitbetreuer, studentFirstname, studentLastname, studentTitle, studentPhone,
                                                        studentMail, vorname_erstbetreuer, nachname_erstbetreuer, titel_erstbetreuer, telefon_erstbetreuer, email_erstbetreuer, 
                                                        firma_name_erstbetreuer, firma_adresse_erstbetreuer, firma_plz_erstbetreuer, firma_stadt_erstbetreuer,
                                                        vorname_zweitbetreuer, nachname_zweitbetreuer, titel_zweitbetreuer, telefon_zweitbetreuer, email_zweitbetreuer,
                                                        firma_name_zweitbetreuer, firma_adresse_zweitbetreuer, firma_plz_zweitbetreuer, firma_stadt_zweitbetreuer, matrikel_nummer, thema_der_arbeit, abstract_der_arbeit);
        LOGGER.info("saved Abschlussarbeit to database");
        return Map.of("database-save", true);
    }

}
