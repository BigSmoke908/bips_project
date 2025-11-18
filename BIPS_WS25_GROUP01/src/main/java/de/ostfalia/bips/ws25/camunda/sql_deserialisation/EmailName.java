package de.ostfalia.bips.ws25.camunda.sql_deserialisation;

public class EmailName {
    private String fullName;
    private String e_mail;

    public EmailName(String fullName, String mail){
        this.e_mail = mail;
        this.fullName = fullName;
    }

    public String getE_mail() {
        return e_mail;
    }

    public String getFullName() {
        return fullName;
    }
}
