package de.ostfalia.bips.ws25.camunda.Abgabe_pack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import ch.qos.logback.classic.pattern.Util;
import de.ostfalia.bips.ws25.camunda.Option;
import de.ostfalia.bips.ws25.camunda.Utils;
import de.ostfalia.bips.ws25.camunda.sql_deserialisation.Betreuer;
import de.ostfalia.bips.ws25.camunda.sql_deserialisation.EmailName;
import io.camunda.client.annotation.JobWorker;
import io.camunda.client.annotation.Variable;

@Component
public class WorkerAbgabe {

    private static final Logger LOGGER = LoggerFactory.getLogger(WorkerAbgabe.class);

    @JobWorker(type = "projekt_und_seminararbeit_ermitteln")
    public Map<String, Object> projektUndSeminararbeitErmitteln(
            @Variable(name = "username") String username) {

        LOGGER.info("Ermittele Projekt- und Seminararbeit für Student: {}", username);
        boolean hatArbeit = true;
        return Map.of("hatArbeit", hatArbeit);
    }

     @JobWorker(type= "ladeStudenten")
     public Map<String, Object> ladeStudenten(@Variable(name = "name_student") String name,
                                                @Variable(name = "vorname_student") String vorname,
                                                @Variable(name = "mat_nr") String matNr,
                                                @Variable(name = "projekt_seminar_oder_abschluss") String projekt_seminar_oder_abschluss
                                                ){ 
        LOGGER.info("lade Studenten....");    
        Integer studtenID =  Abgabe.getStudentFromDatabase(name, vorname, matNr);
        if(studtenID != null){
            EmailName studEmailName = Abgabe.getStudentNameMail(studtenID);    
            String studentFullName = studEmailName.getFullName();
            String studenMail = studEmailName.getE_mail();

            boolean isAbschlussarbeit = projekt_seminar_oder_abschluss.equals("1") ? false : true;
            Map<String, Object> mapComplete = new HashMap<>();
            List<Option<String>> studentWork =  Abgabe.getStudentWork(studtenID, isAbschlussarbeit);
            if(studentWork.size() == 0){
                LOGGER.info("finished lade Studenten, found student but no work of the given type");
                return Map.of("student_has_registered_work", "0");
            }
            Map<String, Object> studentInfo =  Map.of("studentMail", studenMail, "studentFullName", studentFullName, "student_has_registered_work", "1");

            mapComplete.putAll(Map.of("studentWorkList", studentWork));
            mapComplete.putAll(studentInfo);
            LOGGER.info("finished lade Studenten succesfully");
            return mapComplete;
        }
        LOGGER.info("failed lade Studenten, no student found");
        return Map.of("student_has_registered_work", "0");
                                                       
     }

    @JobWorker(type = "saveKolloquiumsDatenToDatabase")
    public Map<String, Object> saveKolloquiumsDatenToDatabase(
            @Variable(name = "studentId") String id,
            @Variable(name = "kolloquiumDatum") String datum,
            @Variable(name = "raum") String location,
            @Variable(name = "arbeit_id") String arbeit_id,
            @Variable(name = "notes") String notes){

        LOGGER.info("Speichere Kolloquiums-Daten...");
        Abgabe.saveKolloquiumData(datum, location, Integer.parseInt(arbeit_id), notes);
        LOGGER.info("Daten des Kolloquium erfolgreich gespeichert");
        return Map.of("saveOK", true);
    }

    @JobWorker(type = "betreuerBestimmen")
    public Map<String, Object> betreuerBestimmen(@Variable(name = "arbeit_id") String arbeit_id, @Variable(name = "projekt_seminar_oder_abschluss") String is_projekt_seminar_oder_abschluss){

        LOGGER.info("bestimme Betreuer der ausgewählten Arbeit...");
        boolean isAbschlussarbeit = is_projekt_seminar_oder_abschluss.equals("1") ? false : true;
        int studentWorkId = Integer.parseInt(arbeit_id);
        Betreuer erstbetreuer = Abgabe.getErstbetreuerIdForStudentWork(studentWorkId);
        if(erstbetreuer == null){
            LOGGER.info("Betreuer bestimmt, kein Betreuer");
            return Map.of("erstbetreuerName", "");
        }
        String erstbetreuerMail = erstbetreuer.getEmail();
        String erstbetreuerName = erstbetreuer.concatName();
        String usernameErstbetreuer = Utils.getUsernameFromLecturer(erstbetreuer.getId());
        Betreuer zweitbetreuer = Abgabe.getZweitbetreuerIdForStudent(studentWorkId);
        String themaArbeit = Utils.getThemaDerArbeit(studentWorkId, isAbschlussarbeit);
        if(zweitbetreuer == null){
            LOGGER.info("Betreuer bestimmt, kein Zweitbetreuer");
            return Map.of("erstbetreuerName", erstbetreuerName, "erstbetreuerMail", erstbetreuerMail, "ErstbetreuerUsernam", usernameErstbetreuer, 
                        "zweitbetreuerName", "", "zweitbetreuerMail", "", "thema_der_arbeit", themaArbeit);
        }
        String zweitbetreuerMail = zweitbetreuer.getEmail();
        String zweitbetreuerName = zweitbetreuer.concatName();

        

        LOGGER.info("Betreuer bestimmt");
        return Map.of("erstbetreuerName", erstbetreuerName, "erstbetreuerMail", erstbetreuerMail, "ErstbetreuerUsernam", usernameErstbetreuer, 
                        "zweitbetreuerName", zweitbetreuerName, "zweitbetreuerMail", zweitbetreuerMail, "thema_der_arbeit", themaArbeit);
    }

    @JobWorker(type = "abgabeNichtBestaetigtMail")
    public Map<String, Object> abgabeNichtBestaetigtMail(
            @Variable(name = "erstbetreuerName") String erstbetreuerName,
            @Variable(name = "erstbetreuerMail") String erstbetreuerMail,
            @Variable(name = "arbeitTitel") String arbeitTitel,
            @Variable(name = "studentFullName") String studentFullName,
            @Variable(name = "studentMail") String studentMail) {

        LOGGER.info("Sende Mail...");
        String message = "die Arbeit " + arbeitTitel + " wurde nicht bestanden." +
                            "\nErstbetreuer: " + erstbetreuerName;
        Utils.simulateEmail(studentFullName, message, studentMail);
        Utils.simulateEmail(erstbetreuerName, message, erstbetreuerMail);

        return Map.of("mailSent", true);
    }

    @JobWorker(type = "datenGespeicherMail")
    public Map<String, Object> datenGespeichertMail(
            @Variable(name = "erstbetreuerName") String erstbetreuerName,
            @Variable(name = "erstbetreuerMail") String erstbetreuerMail,
            @Variable(name = "thema_der_arbeit") String arbeitTitel,
            @Variable(name = "studentFullName") String studentFullName,
            @Variable(name = "studentMail") String studentMail) {

        LOGGER.info("Sende Mail...");
        String message = "die Arbeit " + arbeitTitel + " wurde bestanden." +
                            "\nErstbetreuer: " + erstbetreuerName; 
        Utils.simulateEmail(studentFullName, message, studentMail);
        Utils.simulateEmail(erstbetreuerName, message, erstbetreuerMail);
        return Map.of("mailSent", true);
    }

    @JobWorker(type = "noteeingetragenMail")
    public Map<String, Object> noteEingetragenMail(
            @Variable(name = "erstbetreuerName") String erstbetreuerName,
            @Variable(name = "erstbetreuerMail") String erstbetreuerMail,
            @Variable(name = "zweitbetreuerName") String zweitbetreuerName,
            @Variable(name = "zweitbetreuerMail") String zweitbetreuerMail,
            @Variable(name = "thema_der_arbeit") String arbeitTitel,
            @Variable(name = "studentFullName") String studentFullName,
            @Variable(name = "studentMail") String studentMail,
            @Variable(name = "Note") String note) {

        LOGGER.info("Sende Mail...");
        String message = "die Arbeit " + arbeitTitel + " wurde erfolgreich bestanden." + 
                            "\nNote: " + note +
                            "\nErstbetreuer: " + erstbetreuerName + 
                            "\nZweitbetreuer: " + zweitbetreuerName; //TODO noch Note hinzufügen
        Utils.simulateEmail(studentFullName, message, studentMail);
        Utils.simulateEmail(erstbetreuerName, message, erstbetreuerMail);
        if(zweitbetreuerMail != null && !zweitbetreuerMail.equals("")){
            Utils.simulateEmail(zweitbetreuerName, message, zweitbetreuerMail);
        }
        LOGGER.info("Abschluss mail gesendet");
        return Map.of("mailSent", true);
    }

    @JobWorker(type = "saveAbgabeToDatabase")
    public Map<String, Object> saveAbgabeToDatabase(
            @Variable(name = "studentId") String id,
            @Variable(name = "abgabeDatum") String datum,
            @Variable(name = "isTeamarbeit") String isTeamarbeit,
            @Variable(name = "arbeit_id") String arbeit_id
            ) {

        LOGGER.info("Speichere Abgabe des Studenten ...");
        Abgabe.saveNoteAndSubmissionToDatabase(null, Integer.parseInt(arbeit_id) , isTeamarbeit);
        LOGGER.info("saved");
        return Map.of("saveOK", true);
    }

    @JobWorker(type = "saveAbschlussArbeitnoteToDatabase")
    public Map<String, Object> saveAbschlussArbeitnoteToDatabase(
            @Variable(name = "studentId") String id,
            @Variable(name = "note") String note,
            @Variable(name = "arbeit_id") String arbeit_id) {

        String isTeamwork = "0";
        LOGGER.info("Speichere Abschlussarbeitsnote für Student ...");
        Abgabe.saveNoteAndSubmissionToDatabase(note, Integer.parseInt(arbeit_id) , isTeamwork);
        LOGGER.info("Note und Submission erfolgreich gepeichert");
        return Map.of("saveOK", true);
    }

}