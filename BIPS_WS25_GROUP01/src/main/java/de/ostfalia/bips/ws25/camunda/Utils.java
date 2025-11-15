package de.ostfalia.bips.ws25.camunda;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class Utils {

    public static Connection establishSQLConnection(){
        try {
            return DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/bips_ws25?useSSL=false&allowPublicKeyRetrieval=true",
                "root",
                "Moto2014"
            );
        } catch (SQLException e) {
            System.out.println("SQL Connection failed: ");
            e.printStackTrace();
            return null;
        }
    }

    public static void simulateEmail(String adressat, String message, String email){
        StringBuilder sb = new StringBuilder();
        sb.append("*********************************************************************");
        sb.append("Email an: ").append(email).append("\n\n");
        sb.append("Sehr geehrter/geehrte Herr/Frau ").append(adressat).append(",\n\n");
        sb.append(message).append("\n\n");
        sb.append("Mit freundlichen Grüßen \n");
        sb.append("Ihre Hochschule \n\n");
        sb.append("*********************************************************************");
        System.out.println(sb.toString());
    }

    public static String[] getPossibleSemester(int amountOfYearsIntoTheFuture){
        LocalDate today =  LocalDate.now();
        int month = today.getMonthValue();
        int currentYear = today.getYear();
        String[] semesterDescriptions = new String[amountOfYearsIntoTheFuture * 2];
        boolean firstSemesterWS = true;
        // zwischen von März bis Oktober zählt als Sommer Semester
        if (month < 9 && month > 2){
            firstSemesterWS = false;
        }

        // alle Semeseter der nächsten x Jahre erzeugen
        for(int i = 0; i< amountOfYearsIntoTheFuture * 2; i++){
            String semester = "WS";
            if((firstSemesterWS && (i % 2 == 1)) || (! firstSemesterWS && (i % 2 == 0))){ 
                semester = "SS";
            }
            String year = String.valueOf(currentYear);
            if(semester.compareTo("WS") == 0){
                year = String.valueOf(currentYear) + "/" + String.valueOf(currentYear+1);
            }
            semesterDescriptions[i] = semester + " " + year;

            //immer wenn gerade WS, dann ist dass nächste Semester im nächsten Jahr
            if(semester.compareTo("WS") == 0){
                currentYear ++;
            }
        }

        insertMissingSemester(semesterDescriptions); //add semester to the database if they are not already there
        return semesterDescriptions;

    }

    public static void insertMissingSemester(String[] semester){
        Connection connection = establishSQLConnection();
        String checkQuery = "SELECT COUNT(*) FROM semester WHERE description = ?";
        String insertQuery = "INSERT INTO semester (description) VALUES (?)";

        for(String semesterString : semester){
            try {
                final PreparedStatement statement = connection.prepareStatement(checkQuery);
                statement.setString(1, semesterString);
                final ResultSet result = statement.executeQuery();
                result.next();
                int count = result.getInt(1);
                if(count == 0){
                    final PreparedStatement statementInsert = connection.prepareStatement(insertQuery);
                    statementInsert.setString(1, semesterString);
                    statementInsert.executeUpdate();
                }
            } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            
        }    
    }

    public static int getSemesterIdFromString(String semester){
        Connection connection = establishSQLConnection(); 
        String query = "SELECT id FROM semester WHERE description = ?";
        Integer id = null;

        try (PreparedStatement stmt = connection.prepareStatement(query)) { //TODO am besten immer autoclose
            stmt.setString(1, semester);
            final ResultSet result = stmt.executeQuery();

            if (result.next()) {
                id = result.getInt("id");
            }

            result.close(); //TODO überall noch close aufrufen für result
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return id;
    }

}
