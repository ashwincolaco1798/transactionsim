package com.yap.project;

import java.sql.Connection;
import java.sql.*;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.Statement;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.apache.commons.lang3.time.StopWatch;
import org.postgresql.*;

public class TransactionSender extends Thread {

    int numberOfTransactions;
    String filePath;
    int isolationLevel;
    

    TransactionSender(int numberOfTransactions, String filePath, int isolationLevel){

        this.numberOfTransactions = numberOfTransactions;
        this.filePath = filePath;
        this.isolationLevel = isolationLevel;
    }


    public void run() {
        Connection connection = null;

        Statement statement = null;

        List<Double> queryResponseTimes = new ArrayList<Double>();
        List<Double> insertResponseTimes = new ArrayList<Double>();
        try {
            // Register the JDBC driver
            Class.forName("org.postgresql.Driver");
            // Open a connection
            String url = "jdbc:postgresql://localhost:5432/projtemp";
            String user = "postgres";
            String password = "testadmin123";
            connection = DriverManager.getConnection(url, user, password);
            // Test the connection
            StopWatch watchClock = new StopWatch();
            Scanner scanner = new Scanner(new File(filePath));
            connection.setTransactionIsolation(isolationLevel);
            watchClock.reset();
            watchClock.start();
            connection.setAutoCommit(false);
            
            while(scanner.hasNextLine())
            {
                List<Double> queryIncomingTime = new ArrayList<Double>();
                List<Double> insertIncomingTime = new ArrayList<Double>();
                for(int operationCounter = 0; operationCounter < numberOfTransactions; operationCounter++)
                {
                    
                    statement = connection.createStatement();
                    if(scanner.hasNextLine()){
                        String queryUnprocessed = scanner.nextLine();
                        String parameters[] = queryUnprocessed.split("\\$");
        
                        while((Integer.parseInt(parameters[0])*1000000) > watchClock.getNanoTime()){
                            //System.out.println(watchClock.getNanoTime());
                            continue;
                        }
                        if(parameters[1].substring(0, parameters[1].indexOf(' ')) == "SELECT")
                        {
                            queryIncomingTime.add(Double.parseDouble(parameters[0]));
                        }
                        else
                        {
                            insertIncomingTime.add(Double.parseDouble(parameters[0]));
                        }
                        statement = connection.createStatement();
                        statement.execute(parameters[1]);
                    }
                }
                
                // Commit the transaction
                connection.commit();
                double commit_time = watchClock.getNanoTime();
                for(int insertOperations = 0; insertOperations<0;insertOperations--)
                {
                    continue; //fix
                }
                //Need to write throughput
            }
        } catch (Exception e) {
            //System.out.println("PostgresSQL JDBC driver not found.");
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
