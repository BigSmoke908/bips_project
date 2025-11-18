package de.ostfalia.bips.ws25.camunda.sql_deserialisation;

public class IdKindOfSupervisor {
    
    private int id;
    private boolean isLecturer;

    public IdKindOfSupervisor(int id, boolean isLecturer){
        this.id = id;
        this.isLecturer = isLecturer;
    }

    public int getId() {
        return id;
    }

    public boolean getIsLecturer(){
        return this.isLecturer;
    }
}
