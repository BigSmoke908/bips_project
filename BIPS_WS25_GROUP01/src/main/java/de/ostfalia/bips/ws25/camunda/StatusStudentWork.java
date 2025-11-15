package de.ostfalia.bips.ws25.camunda;

public enum StatusStudentWork {
    ANGEMELDET("angemeldet"),
    ABGEGEBEN("abgegeben");

    private String description;


    private StatusStudentWork(String desciption){
        this.description = desciption;
    }

    public String getDescription() {
        return description;
    }
}
