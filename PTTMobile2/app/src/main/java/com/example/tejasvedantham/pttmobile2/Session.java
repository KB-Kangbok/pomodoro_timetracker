package com.example.tejasvedantham.pttmobile2;

import android.media.audiofx.AcousticEchoCanceler;

import java.io.Serializable;

public class Session implements Serializable {

    public  String startingTime;
    public String endingTime;
    public int counter;
    public String id;
    public double hoursWorked;

    public Session(String startingTime, String endingTime, int counter, String id, double hoursWorked) {
        this.startingTime = startingTime;
        this.endingTime = endingTime;
        this.counter = counter;
        this.id = id;
        this.hoursWorked = hoursWorked;
    }

}
