package com.example.tejasvedantham.pttmobile2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;

public class StartPomodoroStep1Activity extends AppCompatActivity {

    public Spinner projectSpinner;
    private String userId;
    private List<Project> projectList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_pomodoro_1);



        projectSpinner = (Spinner) findViewById(R.id.project_spinner);
        List<String> projects = new ArrayList<String>();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            userId = extras.getString("id");
            projectList = (List<Project>) getIntent().getSerializableExtra("projects");
        }
        projects.add("No Associated Project");

        for (Project p: projectList) {
            projects.add(p.projectName);
        }

        String[] dropdownArray = projects.toArray(new String[0]);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, dropdownArray);
        projectSpinner.setAdapter(adapter);

    }

    public void startPomodoro(View view) {
        Intent intent = new Intent(getApplicationContext(), StartPomodoroStep2Activity.class);
        intent.putExtra("id", userId);
        startActivity(intent);
    }
}
