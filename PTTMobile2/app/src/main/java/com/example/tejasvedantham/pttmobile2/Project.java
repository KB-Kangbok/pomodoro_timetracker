package com.example.tejasvedantham.pttmobile2;

import java.io.Serializable;

public class Project implements Serializable {

    public String projectName;
    public String id;

    public Project(String projectName, String id) {

        this.projectName = projectName;
        this.id = id;
    }

}
