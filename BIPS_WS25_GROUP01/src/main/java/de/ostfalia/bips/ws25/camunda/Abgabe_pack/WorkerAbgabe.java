package de.ostfalia.bips.ws25.camunda.Abgabe_pack;

import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

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
                                                @Variable(name = "mat_nr") String matNr
                                                ){
        //TODO Studenten aus Datenbank holen der passt    
        LOGGER.info("lade STudenten....");                                       
        return Map.of("studentObject", "DB_Mock_Student");                                                
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