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
        TransactionSender t1 = new TransactionSender(5, "/home/yap/tippers/project1/MixedSQLCommand/low_concurrency/sample_10000.txt",Connection.TRANSACTION_READ_COMMITTED);
        t1.start();
    }
}
