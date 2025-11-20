package de.ostfalia.bips.ws25.camunda.Abgabe_pack;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import de.ostfalia.bips.ws25.camunda.Option;
import de.ostfalia.bips.ws25.camunda.StatusStudentWork;
import de.ostfalia.bips.ws25.camunda.Utils;
import de.ostfalia.bips.ws25.camunda.sql_deserialisation.EmailName;
import de.ostfalia.bips.ws25.camunda.sql_deserialisation.Betreuer;

public class Abgabe {
    
    public static Integer getStudentFromDatabase(String studentName, String studentFirstName, String studentMatNr){
        String queryString = "Select * from student where firstname = ? and lastname = ? and matrikel_nummer = ?";//TODO TODO TODO and status = ?";

        Connection connection = Utils.establishSQLConnection();
        PreparedStatement statement;
        Integer student_id = null;
        try {
            statement = connection.prepareStatement(queryString);
            statement.setString(1, studentFirstName);
            statement.setString(2, studentName);
            statement.setString(3, studentMatNr);
            //TODO statement.setInt(4, StatusStudentWork.ABGEGEBEN.getNumber());
            final ResultSet result = statement.executeQuery();

            if(result.next()){
                student_id = result.getInt("id");
            }
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        return student_id;
    }

    public static Betreuer getErstbetreuerIdForStudentWork(int studentWorkId){
        String queryStringSupervisor = "Select * from student_work_has_supervisor where student_work_id = ? and is_primary_supervisor = 1";
        String queryStringLecturer = "Select * from student_work_has_lecturer where student_work_id = ? and is_primary_supervisor = 1";

        Connection connection = Utils.establishSQLConnection();
        PreparedStatement statement;
        Integer erstbetreuerId = null;
        boolean isLecturer = true;
        String firstname = null;
        String lastname = null;
        String phone = null;
        String title = null;
        String email = null;
        try {
            statement = connection.prepareStatement(queryStringLecturer);
            statement.setInt(1, studentWorkId);

            ResultSet result = statement.executeQuery();

            if(result.next()){
                erstbetreuerId = result.getInt("lecturer_id");

                statement = connection.prepareStatement("Select * from lecturer where id = ?");
                statement.setInt(1, erstbetreuerId);

                result = statement.executeQuery();
                if(result.next()){
                    firstname = result.getString("firstname");
                    lastname = result.getString("lastname");
                    phone = result.getString("phone");
                    title = result.getString("title");
                    email = result.getString("email");
                }
                
            }
            if(erstbetreuerId == null){ //kein Lectuerer gefunden ==> supervisor suchen
                isLecturer = false;
                statement = connection.prepareStatement(queryStringSupervisor);
                statement.setInt(1, studentWorkId);

                final ResultSet resultSupervisor = statement.executeQuery();

                if(resultSupervisor.next()){
                    erstbetreuerId = resultSupervisor.getInt("supervisor_id");

                    statement = connection.prepareStatement("Select * from supervisor where id = ?");
                    statement.setInt(1, erstbetreuerId);

                    result = statement.executeQuery();
                    if(result.next()){
                        firstname = result.getString("firstname");
                        lastname = result.getString("lastname");
                        phone = result.getString("phone");
                        title = result.getString("title");
                        email = result.getString("email");
                    }
                }else{
                    return null; //Fall kein Betreuer
                }
            }

        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        return new Betreuer(erstbetreuerId, isLecturer, firstname, lastname, phone, email, title);
    };



    public static Betreuer getZweitbetreuerIdForStudent(int studentWorkId){
        //maybe merge with ERstbetreuer function, then can replaxe != 1 mit =0 je nach boolean
        String queryStringSupervisor = "Select * from student_work_has_supervisor where student_work_id = ? and is_primary_supervisor != 1";
        String queryStringLecturer = "Select * from student_work_has_lecturer where student_work_id = ? and is_primary_supervisor != 1";

        Connection connection = Utils.establishSQLConnection();
        PreparedStatement statement;
        Integer zweitBetreuerId = null;
        boolean isLecturer = true;
        String firstname = null;
        String lastname = null;
        String phone = null;
        String title = null;
        String email = null;
        try {
            statement = connection.prepareStatement(queryStringLecturer);
            statement.setInt(1, studentWorkId);

            final ResultSet result = statement.executeQuery();

            if(result.next()){
                zweitBetreuerId = result.getInt("id");
                firstname = result.getString("firstname");
                lastname = result.getString("lastname");
                phone = result.getString("phone");
                title = result.getString("title");
                email = result.getString("email");
            }
            if(zweitBetreuerId == null){ //kein Lectuerer gefunden ==> supervisor suchen
                isLecturer = false;
                statement = connection.prepareStatement(queryStringSupervisor);
                statement.setInt(1, studentWorkId);

                final ResultSet resultSupervisor = statement.executeQuery();

                if(resultSupervisor.next()){
                    zweitBetreuerId = resultSupervisor.getInt("id");
                    firstname = resultSupervisor.getString("firstname");
                    lastname = resultSupervisor.getString("lastname");
                    phone = resultSupervisor.getString("phone");
                    title = resultSupervisor.getString("title");
                    email = resultSupervisor.getString("email");
                }else{
                    return null; //kein Zweitbetreuer
                }
            }

        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        return new Betreuer(zweitBetreuerId, isLecturer, firstname, lastname, phone, email, title);
    }

    //TODO
    public static EmailName getNameMailBetreuer(int studentWorkId, boolean erstbetreuer){
        Connection connection = Utils.establishSQLConnection();
        String queryString = "";
        PreparedStatement statement;
        
        Betreuer zweitBetreuer = getZweitbetreuerIdForStudent(studentWorkId);
        if(erstbetreuer){
            zweitBetreuer = getErstbetreuerIdForStudentWork(studentWorkId);
        }
        int zweitBetreuerId = zweitBetreuer.getId();
        boolean zweitBetreuerIsLectuere = zweitBetreuer.getIsLecturer();
        if(zweitBetreuerIsLectuere){
            queryString = "Select * from lecturer where id = ?";
        }else{
             queryString = "Select * from supervisor where id = ?";
        }
        try {
            statement = connection.prepareStatement(queryString);
            statement.setInt(1, zweitBetreuerId);
            final ResultSet result = statement.executeQuery();

            if(result.next()){
                String titelZweitbetreuer = result.getString("title");
                String name = result.getString("lastname");
                String firstname = result.getString("firstname");
                String mail = result.getString("email");

                String fullname = titelZweitbetreuer + " " + firstname + " " + name;
                return new EmailName(fullname, mail);
            }

        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return null;
    }

    public static List<Option<String>> getStudentWork(int studentId, boolean isAbschlussarbeit){

        //TODO no student work found
        Connection connection = Utils.establishSQLConnection();
        String queryString = "Select sw.id, sw.type_of_student_work_id, tsw.id from student_work sw " +
                                "join type_of_student_work tsw on sw.type_of_student_work_id = tsw.id " + 
                                "where student_id = ? and (tsw.id = ? or tsw.id = ?)";
        try {
            final PreparedStatement statement = connection.prepareStatement(queryString);
            statement.setInt(1, studentId);
            if(isAbschlussarbeit){
                statement.setInt(2, 3);
                statement.setInt(3, 3);
            }else{
                statement.setInt(2, 1);
                statement.setInt(3, 2);
            }
            
            final ResultSet result = statement.executeQuery();
            List<Option<String>> studentWorkOptions = new ArrayList<>();

            while(result.next()) {
                final String idStudenWork = result.getString("sw.id");
                studentWorkOptions.add(Option.of(getThemaDerArbeit(isAbschlussarbeit, Integer.parseInt(idStudenWork)), idStudenWork));
            }
            return studentWorkOptions;
            //return Map.of("studentWorkList", studentWorkOptions);
            
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static String getThemaDerArbeit(boolean isAbschlussarbeit, int studentWorkId){
        String queryString = null;
        String themaDerArbeit = null;
        Connection connection = Utils.establishSQLConnection();
        if(isAbschlussarbeit){
            queryString = "Select * from thesis where student_work_id = ?";
            try {
                final PreparedStatement statement = connection.prepareStatement(queryString);
                statement.setInt(1, studentWorkId);
                final ResultSet result = statement.executeQuery();
                if(result.next()){
                    themaDerArbeit = result.getString("title");
                }
            }catch (SQLException e) {
                e.printStackTrace();
            }
        }else{
            queryString = "Select * from thema_seminar_projekt where student_work_id = ?";
            try {
                final PreparedStatement statement = connection.prepareStatement(queryString);
                statement.setInt(1, studentWorkId);
                final ResultSet result = statement.executeQuery();
                if(result.next()){
                    themaDerArbeit = result.getString("thema");
                }
            }catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return themaDerArbeit;
    }

    public static EmailName getStudentNameMail(int studentId){
        Connection connection = Utils.establishSQLConnection();
       
        String queryString = "Select * from student where id = ?";
        try {
            final PreparedStatement statement = connection.prepareStatement(queryString);
            statement.setInt(1, studentId);
            final ResultSet result = statement.executeQuery();
            if(result.next()){
                String name = result.getString("lastname");
                String vorname = result.getString("firstname");
                String title = result.getString("title");
                String mail = result.getString("email");
                String fullName = title + " " + vorname + " " + name;
                return new EmailName(fullName, mail);
            }
        }catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static void saveKolloquiumData(String date, String room, int studenWorkId, String notes){
        LocalDate localDate = LocalDate.parse(date);
        Date sqlDate = Date.valueOf(localDate);
        Connection connection = Utils.establishSQLConnection();
       
        String queryString = "Insert into colloquium (student_work_id, date, location, notes) values (?, ?, ?, ?)";
        try {
            final PreparedStatement statement = connection.prepareStatement(queryString);
            statement.setInt(1, studenWorkId);
            statement.setDate(2, sqlDate);
            statement.setString(3, room);
            statement.setString(4, notes);
            statement.executeUpdate();
        }catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void saveNoteAndSubmissionToDatabase(String note, int studentWorkId, String isTeamwork){
        Connection connection = Utils.establishSQLConnection();
        //TODO es gibt noch keine Note im Datenbankmodell?! ==> handle note = null wenn das noch dazu kommt
        String queryString = "Insert into student_work_submission (student_work_id, date_submission, is_teamwork) values (?, ?, ?)";
        LocalDate localDate = LocalDate.now();
        Date sqlDate = Date.valueOf(localDate);
        try {
            final PreparedStatement statement = connection.prepareStatement(queryString);
            statement.setInt(1, studentWorkId);
            statement.setDate(2, sqlDate);
            statement.setString(3, isTeamwork);

            statement.executeUpdate();
        }catch (SQLException e) {
            e.printStackTrace();
        }

        //set status to abgegeben
        queryString = "Update student_work Set status = ?  where id = ?";
        try {
            final PreparedStatement statement = connection.prepareStatement(queryString);
            statement.setInt(1, StatusStudentWork.ABGEGEBEN.getNumber());
            statement.setInt(2, studentWorkId);

            statement.executeUpdate();
        }catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
