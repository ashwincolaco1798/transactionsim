package com.yap.project;

import java.sql.Connection;

/**
 * Hello world!
 *
 */

public class App 
{
    public static void main( String[] args )
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
        ThreadSync syncObj = new ThreadSync();
        int numberOfThreads = 100;
        for(int threadCount = 0; threadCount < numberOfThreads; threadCount++)
        {
            String fileInputName = "/home/yap/tippers/project1/MixedSQLCommand/low_concurrency/MPL_100/Process_"+threadCount+".txt";
            System.out.println(fileInputName);
            String queryResponseName = "queryResponseThread_"+threadCount;
            String insertResponseName = "insertResponseThread_"+ threadCount;
            String commitTimesName = "commitTimeThread_"+threadCount;
            TransactionSender sender = new TransactionSender(5, fileInputName, Connection.TRANSACTION_REPEATABLE_READ , queryResponseName, 
            insertResponseName, commitTimesName,syncObj, numberOfThreads);
            sender.start();
        }
    }
}
