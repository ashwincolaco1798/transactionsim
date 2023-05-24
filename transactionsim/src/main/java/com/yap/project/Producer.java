package com.yap.project;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Producer {

    static List<String> incomingOperations;
    static List<Long> incomingTimes;
    static int cursorID;

    Producer(String workloadFileName) throws FileNotFoundException
    {
        incomingOperations = new ArrayList<String>();
        incomingTimes = new ArrayList<Long>();
        Scanner scanner = new Scanner(new File(workloadFileName));
        while(scanner.hasNextLine())
        {
            String queryUnprocessed = scanner.nextLine();
            String parameters[] = queryUnprocessed.split("\\$");
            long timeValue = Long.parseLong(parameters[0]);
            incomingTimes.add(timeValue);
            incomingOperations.add(parameters[1]);
        }
        cursorID = 0;

    }

    public static synchronized int getCursorValue()
    {
        if(cursorID < incomingTimes.size())
            return cursorID;
        else
        return -1;
    }

    
    public static synchronized int getNextCursor()
    {
        if(cursorID < incomingTimes.size())
            return cursorID++;
        else
        return -1;
    }
}
