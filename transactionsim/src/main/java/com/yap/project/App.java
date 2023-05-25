package com.yap.project;

import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.SQLException;

import org.apache.commons.lang3.time.StopWatch;
/*
 * Authors: Ashwin Gerard Colaco, Yinan Zhou, Pratyoy Das
 */
public class App 
{
    public static void main( String[] args ) throws FileNotFoundException, ClassNotFoundException, SQLException, InterruptedException
    {
        int transactionSizeArray[] = {2,5,10};
        int mplLevelArray[] = {5,50,100,150,200,300};
        int isolationModeArray[] = {Connection.TRANSACTION_READ_COMMITTED,Connection.TRANSACTION_REPEATABLE_READ,Connection.TRANSACTION_SERIALIZABLE};
        String isolationModeStrArray[] = {"TRANSACTION_READ_COMMITTED","TRANSACTION_REPEATABLE_READ","TRANSACTION_SERIALIZABLE"};
        Producer producer = new Producer("/home/yap/tippers/project1/MixedSQLCommand/high_concurrency/mixed_commands.txt");
        for(int transactionSizeExptCounter = 0; transactionSizeExptCounter < 3; transactionSizeExptCounter++)
        {
            int numberOfOperations = transactionSizeArray[transactionSizeExptCounter];
            for(int mplLevelExptCounter = 0; mplLevelExptCounter < 6;mplLevelExptCounter++)
            {
                int numberOfThreads = mplLevelArray[mplLevelExptCounter];
                for(int isolationLevelExptCounter = 0; isolationLevelExptCounter < 3; isolationLevelExptCounter++)
                {
                    Producer.cursorID = 0;
                    new ResetDB();
                    System.out.println("Starting experiment");
                    StopWatch watchClock = new StopWatch();
                    ThreadSync syncObj = new ThreadSync(watchClock, numberOfThreads);
                    int isolationLevel = isolationModeArray[isolationLevelExptCounter];
                    String isolation = isolationModeStrArray[isolationLevelExptCounter];
                    
                    Thread sender[] = new Thread[numberOfThreads];
                    
                    for(int threadCount = 0; threadCount < numberOfThreads; threadCount++)
                    {
                        String queryResponseName = "/home/yap/tippers/Results/High Concurrency/"+numberOfOperations+"/MPL_"+numberOfThreads+"/"+isolation+"/queryResponseThread_"+threadCount;
                        String insertResponseName = "/home/yap/tippers/Results/High Concurrency/"+numberOfOperations+"/MPL_"+numberOfThreads+"/"+isolation+"/insertResponseThread_"+ threadCount;
                        String commitTimesName = "/home/yap/tippers/Results/High Concurrency/"+numberOfOperations+"/MPL_"+numberOfThreads+"/"+isolation+"/commitTimeThread_" + threadCount;
                        sender[threadCount] = new TransactionSender(numberOfOperations, isolationLevel , queryResponseName, 
                        insertResponseName, commitTimesName,syncObj, numberOfThreads, watchClock, producer);
                        sender[threadCount].start();
                    }
                    watchClock.reset();
                    watchClock.start();
                    for(int threadCount = 0; threadCount < numberOfThreads; threadCount++)
                    {
                        sender[threadCount].join();
                    }
                

                }
            }

        }
        
        
        
        
        
    }
}
