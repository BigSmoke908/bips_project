package de.ostfalia.bips.ws25.camunda;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import java.sql.Connection;

public class Anmeldung {
    
    /**
     * loads the lecturers from mysql workbench 
     * @return a map of options of the lecturers
     */
    public static Map<String, Object> ladeDozenten(){
        Connection connection = Utils.establishSQLConnection();
        try {
            final PreparedStatement statement = connection.prepareStatement("SELECT * FROM lecturer");
            final ResultSet result = statement.executeQuery();
            List<Option<String>> lecturerOptions = new ArrayList<>();

            while(result.next()) {
                final String lecturerFirstName = result.getString("firstname");
                final String lecturerLastName = result.getString("lastname");
                final String lecturerTitle = result.getString("title");
                final String lecturerCompleteName = lecturerTitle + " " + lecturerFirstName + " " + lecturerLastName;
                final String lecturerID = result.getString("id");
                lecturerOptions.add(Option.of(lecturerCompleteName, lecturerID));
            }
            return Map.of("dozentenList", lecturerOptions);
            
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static Map<String, Object> ladeStudiengaenge(){
        Connection connection = Utils.establishSQLConnection();
        try {
            final PreparedStatement statement = connection.prepareStatement("SELECT * FROM course_of_studies");
            final ResultSet result = statement.executeQuery();
            List<Option<String>> studiesOptions = new ArrayList<>();

            while (result.next()) {
                final String studyDescription = result.getString("description");
                final String studyId= result.getString("id");
                studiesOptions.add(Option.of(studyDescription, studyId));
            }
            return Map.of("courseOfStudyList", studiesOptions);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Map<String, Object> getAssigneeForBetreuerAbschlussarbeit(String betreuer, String zweitbetreuer, String betreuerSelect, String zweitbetreuerSelect){
        String betreuerIsLecturer = "0";
        String zweitbetreuerIsLecturer = "0"; 
        if(betreuer == null){ 
            betreuerIsLecturer = "1";  //true, wenn es kein Dozent wäre, dann wäre die Variable betreuer gesetzt
            try {       
                if(Constants.replaceActualAssigneesWithDemo){
                    betreuer = "demo";
                }else{
                    Connection connection = Utils.establishSQLConnection();
                    final PreparedStatement statement = connection.prepareStatement("SELECT * FROM lecturer Where id = ?");
                    statement.setString(1, betreuerSelect);
                    final ResultSet result = statement.executeQuery();
                    betreuer = result.getString("username");
                }
                
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        if(zweitbetreuer == null){
            zweitbetreuerIsLecturer = "1"; //true, wenn es kein Dozent wäre, dann wäre die Variable gesetzt
            try {       
                if(Constants.replaceActualAssigneesWithDemo){
                    zweitbetreuer = "demo";
                }else{
                    Connection connection = Utils.establishSQLConnection();
                    final PreparedStatement statement = connection.prepareStatement("SELECT * FROM lecturer Where id = ?");
                    statement.setString(1, zweitbetreuerSelect);
                    final ResultSet result = statement.executeQuery();
                    zweitbetreuer = result.getString("username");
                }
                
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        // We need to know wether the betreuer are lecturers or not to now which entry in database needs to be connected/created
        return Map.of("betreuer_abschlussarbeit", betreuer, "zweitbetreuer", zweitbetreuer, "betreuerIsLecturer", betreuerIsLecturer, "zweitbetreuerIsLecturer", zweitbetreuerIsLecturer);
    }

    /**
     * for anmeldung
     * @return
     */
    public static Map<String, Object> saveProjectOrSeminarWorkToDatabase(String semester, String betreuer, String betreuerIsLecturer, String zweitbetreuer, String zweitbetreuerIsLecturer){
        String status = StatusStudentWork.ANGEMELDET.getDescription();
        return Map.of();
    }

    /**
     * if the student already exists, not new entry is inserted
     * @return the id of the student entry created (or of the entry alredy existing)
     */
    public static int insertStudent(String fistname, String lastname, String title, String phone, String email){
        //TOOD do I need to chekc wether the student already exists?
        // a student is the same if firstname, lastname and email are the same (We assume that the email does not change in ostfalia context)
        return 1;
    }

    public static int insertSupervisorCompany(String firstname, String lastname, String title, String phone, String email, String company){
        return 1;
    }

}
