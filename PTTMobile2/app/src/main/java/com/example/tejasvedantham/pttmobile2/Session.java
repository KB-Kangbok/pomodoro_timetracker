package com.example.tejasvedantham.pttmobile2;

import java.io.Serializable;

public class Session implements Serializable {

    public  String startingTime;
    public String endingTime;
    public int hoursWorked;

    public Session(String startingTime, String endingTime, int hoursWorked) {
        this.startingTime = startingTime;
        this.endingTime = endingTime;
        this.hoursWorked = hoursWorked;
    }

}
