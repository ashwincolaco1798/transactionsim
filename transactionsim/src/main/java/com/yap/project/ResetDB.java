package com.yap.project;

import java.sql.Statement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ResetDB {

    ResetDB() throws ClassNotFoundException, SQLException{
        Connection connection = null;

        Statement statement = null;
        Class.forName("org.postgresql.Driver");
            // Open a connection
        String url = "jdbc:postgresql://localhost:5432/mpl100";
        String user = "postgres";
        String password = "testadmin123";
        connection = DriverManager.getConnection(url, user, password);
        statement = connection.createStatement();
        statement.execute("delete from occupancy;");
        statement.execute("delete from wemoobservation ;");
        statement.execute("delete from wifiapobservation ;");
        statement.execute("delete from thermometerobservation ;");
        statement.execute("delete from presence;");
        connection.close();
    }
    
}
