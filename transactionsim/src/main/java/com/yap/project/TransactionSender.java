package com.yap.project;

import java.sql.Connection;
import java.sql.*;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.Statement;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.postgresql.*;

public class TransactionSender extends Thread {

    int numberOfTransactions;
    String filePath;
    int isolationLevel;
    String queryResponseFile;
    String insertResponseFile;
    String commitFile;

    TransactionSender(int numberOfTransactions, String filePath, int isolationLevel, String queryResponseFile, String insertResponseFile, String commitFile){

        this.numberOfTransactions = numberOfTransactions;
        this.filePath = filePath;
        this.isolationLevel = isolationLevel;
        this.queryResponseFile = queryResponseFile;
        this.insertResponseFile = insertResponseFile;
        this.commitFile = commitFile;
    }


    public void run() {
        Connection connection = null;

        Statement statement = null;

        List<Double> queryResponseTimes = new ArrayList<Double>();
        List<Double> insertResponseTimes = new ArrayList<Double>();
        List<Double> commitTimes = new ArrayList<Double>();
        StopWatch watchClock = new StopWatch();
        try {
            // Register the JDBC driver
            Class.forName("org.postgresql.Driver");
            // Open a connection
            String url = "jdbc:postgresql://localhost:5432/mpl100";
            String user = "postgres";
            String password = "testadmin123";
            connection = DriverManager.getConnection(url, user, password);
            // Test the connection
            
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
                        System.out.println(Long.parseLong(parameters[0]));
                        while((Long.parseLong(parameters[0])) > watchClock.getTime()){
                            System.out.println(watchClock.getTime());
                            continue;
                        }
                        //System.out.println(parameters[1].substring(0, parameters[1].indexOf(' ')));
                        if(parameters[1].substring(0, parameters[1].indexOf(' ')).equals("SELECT"))
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
                //connection.commit();
                double commitTime = watchClock.getTime();
                commitTimes.add(commitTime);
                for(int insertOperations = 0; insertOperations < insertIncomingTime.size();insertOperations++)
                {
                    insertResponseTimes.add(commitTime - insertIncomingTime.get(insertOperations));
                }
                for(int queryOperations = 0; queryOperations < queryIncomingTime.size(); queryOperations++)
                {
                    queryResponseTimes.add(commitTime - queryIncomingTime.get(queryOperations));
                }
                //Need to write throughput
            }
        } catch (Exception e) {
            //System.out.println("PostgresSQL JDBC driver not found.");
            e.printStackTrace();
       } finally {

            System.out.println(watchClock.getTime());
            File queryFileResponseTimes = new File(queryResponseFile);
            File insertFileResponseTimes = new File(insertResponseFile);
            File commitFileTimes = new File(commitFile);
            try {
                FileUtils.writeLines(queryFileResponseTimes, queryResponseTimes, false);
                FileUtils.writeLines(insertFileResponseTimes, insertResponseTimes, false);
                FileUtils.writeLines(commitFileTimes, commitTimes, false);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            
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
