package de.ostfalia.bips.ws25.camunda;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
                final String lecturerCompleteName = lecturerTitle + lecturerFirstName + lecturerLastName;
                final String lecturerID = result.getString("id");
                lecturerOptions.add(Option.of( lecturerCompleteName, lecturerID));
            }
            return Map.of("dozentenList", lecturerOptions);
            
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return null;
    }
}
