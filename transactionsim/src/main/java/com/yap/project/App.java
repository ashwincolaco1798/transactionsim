package com.yap.project;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        TransactionSender t1 = new TransactionSender(5, "/home/yap/tippers/project1/MixedSQLCommand/low_concurrency/sample_10000.txt");
        t1.start();
    }
}
