package com.yap.project;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.Statement;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import org.apache.commons.lang3.time.StopWatch;

public class TransactionSender extends Thread {

    int numberOfTransactions;
    String filePath;

    TransactionSender(int numberOfTransactions, String filePath){

        this.numberOfTransactions = numberOfTransactions;
        this.filePath = filePath;
    }


    public void run() {
        Connection connection = null;
        Statement statement = null;
        try {
            // Register the JDBC driver
            Class.forName("org.postgresql.Driver");
            // Open a connection
            String url = "jdbc:postgresql://localhost:5432/CS223Proj1";
            String user = "postgres";
            String password = "testadmin123";
            connection = DriverManager.getConnection(url, user, password);
            // Test the connection
            StopWatch watchClock = new StopWatch();
            Scanner scanner = new Scanner(new File(filePath));
            if (connection != null) {
                
                watchClock.reset();
                connection.setAutoCommit(false);
                while(!scanner.hasNextLine())
                {
                    for(int transactionCounter = 0; transactionCounter < numberOfTransactions; transactionCounter++)
                    {
                        statement = connection.createStatement();
                        if(scanner.hasNextLine()){
                            String queryUnprocessed = scanner.nextLine();
                            String parameters[] = queryUnprocessed.split("$");
                            while((Integer.parseInt(parameters[0])*1000000) > watchClock.getNanoTime()){
                                continue;
                            }
                            statement = connection.createStatement();
                            statement.execute(parameters[1]);
                        }
                    }
                    
                    // Commit the transaction
                    connection.commit();
                }
                
            } else {
                System.out.println("Failed to make connection!");
            }
        } catch (ClassNotFoundException e) {
            System.out.println("PostgresSQL JDBC driver not found.");
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("Connection failed!");
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            // Close the connection
            try {
                if (connection != null) {
                    connection.close();
                    System.out.println("Connection closed.");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
