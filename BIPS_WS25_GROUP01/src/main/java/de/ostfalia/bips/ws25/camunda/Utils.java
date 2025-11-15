package de.ostfalia.bips.ws25.camunda;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

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
}
