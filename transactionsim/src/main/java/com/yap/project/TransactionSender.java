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

    int numberOfOperations;
    String filePath;
    int isolationLevel;
    String queryResponseFile;
    String insertResponseFile;
    String commitFile;
    ThreadSync syncObject;
    int numberOfThreads;
    StopWatch watchClock;
    Producer producer;

    TransactionSender(int numberOfOperations,int isolationLevel, String queryResponseFile, 
    String insertResponseFile, String commitFile, ThreadSync syncObject, int numberOfThreads, StopWatch watchClock, Producer producer){

        this.numberOfOperations = numberOfOperations;
        this.isolationLevel = isolationLevel;
        this.queryResponseFile = queryResponseFile;
        this.insertResponseFile = insertResponseFile;
        this.commitFile = commitFile;
        this.syncObject = syncObject;
        this.numberOfThreads = numberOfThreads;
        this.watchClock = watchClock;
        this.producer = producer;
    }


    public void run() {
        Connection connection = null;

        Statement statement = null;

        List<Long> queryResponseTimes = new ArrayList<Long>();
        List<Long> insertResponseTimes = new ArrayList<Long>();
        List<Long> commitTimes = new ArrayList<Long>();
        try {
            // Register the JDBC driver
            Class.forName("org.postgresql.Driver");
            // Open a connection
            String url = "jdbc:postgresql://localhost:5432/mpl100";
            String user = "postgres";
            String password = "testadmin123";
            connection = DriverManager.getConnection(url, user, password);
            
            syncObject.increment();
            while(syncObject.get()<numberOfThreads)
            {
                System.out.println(syncObject.get());
                continue;
            }//all threads should reach here at the same time
            System.out.println("Breakout");

            connection.setTransactionIsolation(isolationLevel);
            connection.setAutoCommit(false);
            statement = connection.createStatement();

            
            while(Producer.getCursorValue()!=-1)
            {
                try{
                
                List<Long> queryIncomingTime = new ArrayList<Long>();
                List<Long> insertIncomingTime = new ArrayList<Long>();
                for(int operationCounter = 0; operationCounter < numberOfOperations; operationCounter++)
                {
                    
                    int currentCursor = Producer.getNextCursor();
                    if(currentCursor < 0)
                    break;
                    long currentTime = Producer.incomingTimes.get(currentCursor);
                    String currentOperation = Producer.incomingOperations.get(currentCursor);
                    sleep(currentTime>watchClock.getTime()?currentTime-watchClock.getTime():0);
                    // while(currentTime > watchClock.getTime()){
                    //     continue;
                    // }
                    // System.out.println(parameters[1].substring(0, parameters[1].indexOf(' ')));
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
            }
                catch(SQLException e)
                {
                    //System.out.println("Transaction Aborted");
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
                FileUtils.writeLines(commitFileTimes, commitTimes, true);
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
