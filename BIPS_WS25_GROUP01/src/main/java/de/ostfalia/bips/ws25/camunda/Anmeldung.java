package de.ostfalia.bips.ws25.camunda;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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

    public static Map<String, Object> ladeSemester(){
        String[] semester = Utils.getPossibleSemester(Constants.AMOUNT_FUTURE_SEMESTER);
        List<Option<String>> semesterOptions = new ArrayList<>();
        for(String semesterString : semester){
            semesterOptions.add(Option.of(semesterString, semesterString)); //this is fine, as the String representation is the same found in the database
        }
        return Map.of("semesterList", semesterOptions);
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
    //TODO Übergabe als Objekte für Übersichlichkeit ==> Student, Supervisor, Company, Lecturer?
    public static Map<String, Object> saveProjectOrSeminarWorkToDatabase(String semester, String projektarbeit, String betreuerVorhanden, String betreuerExtern, String studiengang, String lecturerId,
                                                                            String firstnameStudent, String lastnameStudent, String titleStudent, String phoneStudent, String emailStudent, //student
                                                                            String firstnameExternSuper, String lastnameExternSuper, String titleExternSuper, String phoneExternSuper, String emailExternSuper, //supervisor
                                                                            String companyName, String address, String zipCode, String city){ //company
        
        Connection connection = Utils.establishSQLConnection();
        Integer studentWorkId = null;
        try {
            Statement maxIdStmt = connection.createStatement();
            ResultSet maxIdRs = maxIdStmt.executeQuery("SELECT COALESCE(MAX(id), 0) + 1 AS new_id FROM company");
            studentWorkId = maxIdRs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        String status = StatusStudentWork.ANGEMELDET.getDescription();
        String type_of_student_work_id;
        if(Integer.parseInt(projektarbeit) == 1){
            //TODO schön machen mit key raussuchen aus DB
            type_of_student_work_id = "1"; //projektarbeit
        }else{
            type_of_student_work_id = "2"; //seminararbeit
        }
        String semester_id = String.valueOf(Utils.getSemesterIdFromString(semester));
        //Studiengang already is a number (inside of the String)
        int idStudent = insertStudent(firstnameStudent, lastnameStudent, titleStudent, phoneStudent, emailStudent);

        //check wether we have a supervisor, then insert the correct one (extern or dozent)
        if(betreuerVorhanden.equals("1")){
            if(betreuerExtern.equals("1")){
                int supervisorId = insertSupervisorFromCompany(firstnameExternSuper, lastnameExternSuper, titleExternSuper, phoneExternSuper, emailExternSuper, companyName, address, zipCode, city);
                insertStudentWorkHasSupervisor(String.valueOf(studentWorkId), String.valueOf(supervisorId), "1"); // 1 for true and is alway primary supervisor
            }else{
                insertStudentWorkHasLecturer(String.valueOf(studentWorkId), lecturerId, "1");
            }
        }

        insertStudentWork(idStudent, status, Integer.parseInt(type_of_student_work_id), Integer.parseInt(semester_id), Integer.parseInt(studiengang), idStudent);

        return Map.of();
    }

    public static void insertStudentWork(int studentWorkId, String status, int typeOfStudentWorkId, int semesterId, int courseOfStudiesId, int studentId){
        String insertQuery = "INSERT INTO student (id, status, type_of_student_work_id, semester_id, course_of_studies_id, student_id) VALUES (?, ?, ?, ?, ?, ?)";
        try(Connection connection = Utils.establishSQLConnection();) {
            final PreparedStatement statement = connection.prepareStatement(insertQuery);
            statement.setInt(1, studentWorkId);
            statement.setString(2, status);
            statement.setInt(3, typeOfStudentWorkId);
            statement.setInt(4, semesterId);
            statement.setInt(5, courseOfStudiesId);
            statement.setInt(6, studentId);
            statement.executeQuery();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static void insertStudentWorkHasSupervisor(String studentWorkId, String supervisorId, String isPrimarySupervisor){
        String insertQuery = "INSERT INTO student (student_work_id, supervisor_id, is_primary_supervisor) VALUES (?, ?, ?)";
        try(Connection connection = Utils.establishSQLConnection();) {
            final PreparedStatement statement = connection.prepareStatement(insertQuery);
            //TODO change setString to setInt whereever it should (not necessary, but highly recommended)
            statement.setString(1, studentWorkId);
            statement.setString(2, supervisorId);
            statement.setString(3, isPrimarySupervisor);
            statement.executeQuery();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static void insertStudentWorkHasLecturer(String studentWorkId, String lecturerId, String isPrimarySupervisor){
        String insertQuery = "INSERT INTO student (student_work_id, lecturer_id, is_primary_supervisor, is_billed) VALUES (?, ?, ?, ?)";
        //TODO isbilled???
        String isBilled = "1";

        try(Connection connection = Utils.establishSQLConnection();) {
            final PreparedStatement statement = connection.prepareStatement(insertQuery);
            statement.setString(1, studentWorkId);
            statement.setString(2, lecturerId);
            statement.setString(3, isPrimarySupervisor);
            statement.setString(4, isBilled);
            statement.executeQuery();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * if the student already exists, no new entry is inserted
     * @return the id of the student entry created (or of the entry alredy existing)
     */
    public static int insertStudent(String firstname, String lastname, String title, String phone, String email){
        // a student is the same if firstname, lastname and email are the same (We assume that the email does not change in ostfalia context)
        String checkQuery = "SELECT COUNT(*) FROM student WHERE firstname = ? AND lastname = ? and email = ?";
        String insertQuery = "INSERT INTO student (firstname, lastname, title, phone, email) VALUES (?, ?, ?, ?, ?)";
        String updateQuery = "UPDATE student SET title = ?, phone = ? WHERE firstname = ? AND lastname = ? AND email = ?";

        try(Connection connection = Utils.establishSQLConnection();) {
            final PreparedStatement statement = connection.prepareStatement(checkQuery);
            statement.setString(1, firstname);
            statement.setString(2, lastname);
            statement.setString(3, email);
            final ResultSet result = statement.executeQuery();

            if(result.next()){
                if(result.getInt(1) == 0){ //first col will be count ==> 0 insert needed
                    final PreparedStatement insertStatement = connection.prepareStatement(insertQuery);
                    insertStatement.setString(1, firstname);
                    insertStatement.setString(2, lastname);
                    insertStatement.setString(3, title);
                    insertStatement.setString(4, phone);
                    insertStatement.setString(5, email);
                    insertStatement.executeQuery();

                    ResultSet generatedKeys = insertStatement.getGeneratedKeys();
                    if (generatedKeys.next()) {
                        return generatedKeys.getInt(1);
                    }

                }else{ //student already exist, just update phone and title just in case
                    final PreparedStatement updateStatement = connection.prepareStatement(updateQuery);
                    updateStatement.setString(1, title);
                    updateStatement.setString(2, phone);
                    updateStatement.setString(3, firstname);
                    updateStatement.setString(4, lastname);
                    updateStatement.setString(5, email);
                    updateStatement.executeQuery();
                    return result.getInt("id");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return -1; 
    }

    public static int insertSupervisorFromCompany(String firstname, String lastname, String title, String phone, String email,
                                                     String companyName, String address, String zipCode, String city){

        int companyId = insertCompany(companyName, address, zipCode, city);

        String checkQuery = "SELECT id FROM supervisor WHERE firstname = ? AND lastname = ? AND email = ? AND company_id = ?";
        String insertQuery = "INSERT INTO supervisor (firstname, lastname, title, phone, email, company_id) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection connection = Utils.establishSQLConnection()) {
            PreparedStatement checkStmt = connection.prepareStatement(checkQuery);
            checkStmt.setString(1, firstname);
            checkStmt.setString(2, lastname);
            checkStmt.setString(3, email);
            checkStmt.setInt(4, companyId);
            //We do not use phone and title to check, as these can easily change
            ResultSet result = checkStmt.executeQuery();

            if (result.next()) {
                return result.getInt("id");
            } else {
                PreparedStatement insertStatement = connection.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS);
                insertStatement.setString(1, firstname);
                insertStatement.setString(2, lastname);
                insertStatement.setString(3, title);
                insertStatement.setString(4, phone);
                insertStatement.setString(5, email);
                insertStatement.setInt(6, companyId);
                insertStatement.executeUpdate();

                ResultSet keys = insertStatement.getGeneratedKeys();
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return -1;
    }

    public static int insertCompany(String companyName, String address, String zipCode, String city){
        String checkQuery = "SELECT id FROM company WHERE description = ? AND address = ? AND zip_code = ? AND city = ?";
        String insertQuery = "INSERT INTO company (id, description, address, zip_code, city) VALUES (?, ?, ?, ?, ?)";

        try (Connection connection = Utils.establishSQLConnection()) {
            PreparedStatement checkStmt = connection.prepareStatement(checkQuery);
            checkStmt.setString(1, companyName);
            checkStmt.setString(2, address);
            checkStmt.setString(3, zipCode);
            checkStmt.setString(4, city);
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("id");
            } else {
                // Generate a new ID manually (since it's not auto-increment)
                Statement maxIdStmt = connection.createStatement();
                ResultSet maxIdRs = maxIdStmt.executeQuery("SELECT COALESCE(MAX(id), 0) + 1 AS new_id FROM company"); //TODO add autoincrement, then chage this
                maxIdRs.next();
                int newId = maxIdRs.getInt("new_id");

                PreparedStatement insertStmt = connection.prepareStatement(insertQuery);
                insertStmt.setInt(1, newId);
                insertStmt.setString(2, companyName);
                insertStmt.setString(3, address);
                insertStmt.setString(4, zipCode);
                insertStmt.setString(5, city);
                insertStmt.executeUpdate();

                return newId;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return -1;
    }

}
