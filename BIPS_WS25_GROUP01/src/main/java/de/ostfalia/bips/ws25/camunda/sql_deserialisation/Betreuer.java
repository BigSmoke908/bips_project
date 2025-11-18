package de.ostfalia.bips.ws25.camunda.sql_deserialisation;

public class Betreuer {
    
    private int id;
    private boolean isLecturer;
    private String firstname;
    private String lastname;
    private String phone;
    private String email;
    private String title;

    public Betreuer(int id, boolean isLecturer, String firstname, String lastname, String phone, String email, String title){
        this.id = id;
        this.isLecturer = isLecturer;
        this.firstname = firstname;
        this.lastname = lastname;
        this.phone = phone;
        this.email = email;
        this.title = title;
    }

    public int getId() {
        return id;
    }

    public boolean getIsLecturer(){
        return this.isLecturer;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }
    
    public void setId(int id) {
        this.id = id;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
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

    public String getTitle() {
        return title;
    }

    public String concatName(){
        return getTitle() + " " + getFirstname() + " " + getLastname();
    }
}
