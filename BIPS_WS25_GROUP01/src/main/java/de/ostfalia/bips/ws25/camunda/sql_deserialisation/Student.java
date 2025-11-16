package de.ostfalia.bips.ws25.camunda.sql_deserialisation;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import de.ostfalia.bips.ws25.camunda.Utils;

public class Student {
    private int id;
    private String firstname;
    private String lastname;
    private String phone;
    private String email;
    private String title;

    public Student(int id, String firstname, String lastname, String phone, String email, String title) {
        this.id = id;
        this.firstname = firstname;
        this.lastname = lastname;
        this.phone = phone;
        this.email = email;
        this.title = title;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getId() {
        return id;
    }

    public String getFirstname() {
        return firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }

    public String getTitle() {
        return title;
    }

    public static Student getStudentFromId(int id) {
        Connection connection = Utils.establishSQLConnection();
        String query = "SELECT * FROM student WHERE id = ?";
        Student resultStudent = null;

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, id); 
            final ResultSet result = stmt.executeQuery();

            if (result.next()) {
                String firstname = result.getString("firstname");
                String lastname = result.getString("lastname");
                String title = result.getString("title");
                String phone = result.getString("phone");
                String email = result.getString("email");

                return new Student(id, firstname, lastname, phone, email, title);
            }

            result.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return resultStudent;
    }
}

