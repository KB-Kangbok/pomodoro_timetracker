package com.example.tejasvedantham.pttmobile2;

import android.os.Bundle;
import android.widget.ListView;

import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;

public class UserHomeActivity extends AppCompatActivity {

    private ListView projectListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activitiy_user_home);

        projectListView = (ListView) findViewById(R.id.project_list);
        ArrayList<Project> projectList = new ArrayList<Project>();
        ProjectListAdapter adapter = new ProjectListAdapter(this, projectList);

        //TODO: Add projects to projectList and update adapter
    }


}
