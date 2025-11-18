package de.ostfalia.bips.ws25.camunda.Abgabe_pack;

import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

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
            Map<String, Object> studentWork =  Abgabe.getStudentWork(studtenID, isAbschlussarbeit);
            if(studentWork.isEmpty()){
                LOGGER.info("finished lade Studenten, found student but no work of the given type");
                return Map.of("student_has_registered_work", "0");
            }
            Map<String, Object> studentInfo =  Map.of("studentMail", studenMail, "studentFullName", studentFullName, "student_has_registered_work", "1");

            mapComplete.putAll(studentWork);
            mapComplete.putAll(studentInfo);
            LOGGER.info("finished lade Studenten succesfully");
            return mapComplete;
        }
        LOGGER.info("failed lade Studenten, no student found");
        return Map.of("student_has_registered_work", "0");
                                                       
     }
    @JobWorker(type = "saveAbschlussArbeitToDatabase")
    public Map<String, Object> saveAbschlussArbeitToDatabase(
            @Variable(name = "studentId") String studentId,
            @Variable(name = "arbeitTitel") String titel) {

        LOGGER.info("Speichere Abschlussarbeit für Student {} in DB…", studentId);
        return Map.of("abschlussarbeit_gefunden", "1");
    }

    @JobWorker(type = "keineArbeitgefundenMail")
    public Map<String, Object> keineArbeitGefundenMail(
            @Variable(name = "username") String username) {

        LOGGER.info("Sende Mail: Keine Arbeit gefunden für {}", username);
        return Map.of("mailSent", true);
    }

    @JobWorker(type = "saveAbgabeToDatabase")
    public Map<String, Object> saveAbgabeToDatabase(
            @Variable(name = "studentId") String id,
            @Variable(name = "abgabeDatum") String datum) {

        LOGGER.info("Speichere Abgabe des Studenten {} in DB…", id);
        return Map.of("saveOK", true);
    }

    @JobWorker(type = "abgabebestaetigtMail")
    public Map<String, Object> abgabeBestaetigtMail(
            @Variable(name = "username") String username) {

        LOGGER.info("Sende Abgabebestätigung an {}", username);
        return Map.of("mailSent", true);
    }

    @JobWorker(type = "datenGespeicherMail")
    public Map<String, Object> datenGespeichertMail(
            @Variable(name = "username") String username) {

        LOGGER.info("Sende Mail: Daten gespeichert an {}", username);
        return Map.of("mailSent", true);
    }

    @JobWorker(type = "saveKolloquiumsDatenToDatabase")
    public Map<String, Object> saveKolloquiumsDatenToDatabase(
            @Variable(name = "studentId") String id,
            @Variable(name = "kolloquiumDatum") String datum) {

        LOGGER.info("Speichere Kolloquiums-Daten für Student {}…", id);
        return Map.of("saveOK", true);
    }

    @JobWorker(type = "saveAbschlussArbeitnoteToDatabase")
    public Map<String, Object> saveAbschlussArbeitnoteToDatabase(
            @Variable(name = "studentId") String id,
            @Variable(name = "note") String note) {

        LOGGER.info("Speichere Abschlussarbeitsnote für Student {}: {}", id, note);
        return Map.of("saveOK", true);
    }

    @JobWorker(type = "noteeingetragenMail")
    public Map<String, Object> noteEingetragenMail(
            @Variable(name = "username") String username) {

        LOGGER.info("Sende Mail: Note eingetragen an {}", username);
        return Map.of("mailSent", true);
    }
}