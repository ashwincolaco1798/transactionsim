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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.prefs.InvalidPreferencesFormatException;

import org.apache.commons.collections4.MultiMap;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.postgresql.*;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

public class TransactionSender extends Thread {

    int numberOfTransactions;
    String filePath;
    int isolationLevel;
    String queryResponseFile;
    String insertResponseFile;
    String commitFile;
    ThreadSync syncObject;
    int numberOfThreads;

    TransactionSender(int numberOfTransactions, String filePath, int isolationLevel, String queryResponseFile, 
    String insertResponseFile, String commitFile, ThreadSync syncObject, int numberOfThreads){

        this.numberOfTransactions = numberOfTransactions;
        this.filePath = filePath;
        this.isolationLevel = isolationLevel;
        this.queryResponseFile = queryResponseFile;
        this.insertResponseFile = insertResponseFile;
        this.commitFile = commitFile;
        this.syncObject = syncObject;
        this.numberOfThreads = numberOfThreads;
    }


    public void run() {
        Connection connection = null;

        Statement statement = null;

        List<Long> queryResponseTimes = new ArrayList<Long>();
        List<Long> insertResponseTimes = new ArrayList<Long>();
        List<Long> commitTimes = new ArrayList<Long>();
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
            List<Long> incomingTimes = new ArrayList<Long>();
            List<String> incomingOperations = new ArrayList<String>();
            while(scanner.hasNextLine())
            {
                String queryUnprocessed = scanner.nextLine();
                String parameters[] = queryUnprocessed.split("\\$");
                long timeValue = Long.parseLong(parameters[0]);
                incomingTimes.add(timeValue);
                incomingOperations.add(parameters[1]);
            }
            //need to add synchronizer here

            syncObject.increment();
            while(syncObject.get()<numberOfThreads)
            {
                System.out.println(syncObject.get());
                continue;
            }
            System.out.println("Breakout");
            connection.setTransactionIsolation(isolationLevel);
            watchClock.reset();
            watchClock.start();
            connection.setAutoCommit(false);
            statement = connection.createStatement();
            
            for(int counterTotal = 0; counterTotal < incomingTimes.size(); counterTotal+=5)
            {
                
                List<Long> queryIncomingTime = new ArrayList<Long>();
                List<Long> insertIncomingTime = new ArrayList<Long>();
                for(int operationCounter = 0; operationCounter < numberOfTransactions; operationCounter++)
                {
                    
                    
                    if(operationCounter+counterTotal >= incomingTimes.size()){
                        break;
                    }
                    long currentTime = incomingTimes.get(counterTotal+operationCounter);
                    String currentOperation = incomingOperations.get(counterTotal+operationCounter);
                    //System.out.println(Long.parseLong(parameters[0]));
                    while(incomingTimes.get(counterTotal+operationCounter) > watchClock.getTime()){
                        //System.out.println(watchClock.getTime());
                        continue;
                    }
                    //System.out.println(parameters[1].substring(0, parameters[1].indexOf(' ')));
                    if(currentOperation.substring(0, currentOperation.indexOf(' ')).equals("SELECT"))
                    {
                        queryIncomingTime.add(currentTime);
                    }
                    else
                    {
                        insertIncomingTime.add(currentTime);
                    }
                    
                    statement.execute(currentOperation);
                }
                
                // Commit the transaction
                connection.commit();
                long commitTime = watchClock.getTime();
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
