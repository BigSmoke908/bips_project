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
}
