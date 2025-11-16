package de.ostfalia.bips.ws25.camunda;

public enum StatusStudentWork {
    ANGEMELDET("angemeldet", 1),
    ABGEGEBEN("abgegeben", 2);

    private String description;
    private int number;

    private StatusStudentWork(String desciption, int number){
        this.description = desciption;
        this.number = number;
    }

    public String getDescription() {
        return description;
    }

    public int getNumber() {
        return number;
    }
}
