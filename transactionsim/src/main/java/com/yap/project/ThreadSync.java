package com.yap.project;

import org.apache.commons.lang3.time.StopWatch;

public class ThreadSync {

    static int numberOfThreads;
    int maxThreads;
    StopWatch watchClock;
    ThreadSync(StopWatch watchClock, int maxThreads)
    {
        numberOfThreads = 0;
        this.watchClock = watchClock;
        this.maxThreads = maxThreads;
    }

    synchronized void increment(){

        
        numberOfThreads++;
        System.out.println("Number of Threads Done " + numberOfThreads);
        if(numberOfThreads==maxThreads-1)
            watchClock.start();
    }
    int get()
    {
        return numberOfThreads;
    }
    
}
