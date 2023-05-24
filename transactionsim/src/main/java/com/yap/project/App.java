package com.yap.project;

import java.io.FileNotFoundException;
import java.sql.Connection;

import org.apache.commons.lang3.time.StopWatch;

/**
 * Hello world!
 *
 */

public class App 
{
    public static void main( String[] args ) throws FileNotFoundException
    {
        // TransactionSender t1 = new TransactionSender(5, 
        // "/home/yap/tippers/project1/MixedSQLCommand/low_concurrency/MPL_5/Process_0.txt",
        // Connection.TRANSACTION_READ_COMMITTED, "x1", "y1","z1");
        // TransactionSender t2 = new TransactionSender(5, 
        // "/home/yap/tippers/project1/MixedSQLCommand/low_concurrency/MPL_5/Process_1.txt",
        // Connection.TRANSACTION_READ_COMMITTED, "x2", "y2","z2");
        // TransactionSender t3 = new TransactionSender(5, 
        // "/home/yap/tippers/project1/MixedSQLCommand/low_concurrency/MPL_5/Process_2.txt",
        // Connection.TRANSACTION_READ_COMMITTED, "x3", "y3","z3");
        // TransactionSender t4 = new TransactionSender(5, 
        // "/home/yap/tippers/project1/MixedSQLCommand/low_concurrency/MPL_5/Process_3.txt",
        // Connection.TRANSACTION_READ_COMMITTED, "x4", "y4","z4");
        // TransactionSender t5 = new TransactionSender(5, 
        // "/home/yap/tippers/project1/MixedSQLCommand/low_concurrency/MPL_5/Process_4.txt",
        // Connection.TRANSACTION_READ_COMMITTED, "x5", "y5","z5");
        // t1.start();
        // t2.start();
        // t3.start();
        // t4.start();
        // t5.start();
        int numberOfThreads = 200;
        int numberOfOperations = 5;
        StopWatch watchClock = new StopWatch();
        ThreadSync syncObj = new ThreadSync(watchClock, numberOfThreads);
        
        
        Producer producer = new Producer("/home/yap/tippers/project1/MixedSQLCommand/low_concurrency/mixed_commands.txt");
        for(int threadCount = 0; threadCount < numberOfThreads; threadCount++)
        {
            String queryResponseName = "/home/yap/tippers/project1/MixedSQLCommand/low_concurrency/MPL_"+numberOfThreads+"/Serializable/queryResponseThread_"+threadCount;
            String insertResponseName = "/home/yap/tippers/project1/MixedSQLCommand/low_concurrency/MPL_"+numberOfThreads+"/Serializable/insertResponseThread_"+ threadCount;
            String commitTimesName = "/home/yap/tippers/project1/MixedSQLCommand/low_concurrency/MPL_"+numberOfThreads+"/Serializable/commitTimesMerged";
            TransactionSender sender = new TransactionSender(numberOfOperations, Connection.TRANSACTION_READ_COMMITTED , queryResponseName, 
            insertResponseName, commitTimesName,syncObj, numberOfThreads, watchClock, producer);
            sender.start();
        }
        
    }
}
