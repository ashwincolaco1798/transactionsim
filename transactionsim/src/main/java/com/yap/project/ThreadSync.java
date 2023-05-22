package com.yap.project;

public class ThreadSync {

    static int numberOfThreads;
    ThreadSync()
    {
        numberOfThreads = 0;
    }

    void increment(){

        
        numberOfThreads++;
        System.out.println("Number of Threads Done " + numberOfThreads);
    }
    int get()
    {
        return numberOfThreads;
    }
    
}
