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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_pomodoro_1);

        projectSpinner = (Spinner) findViewById(R.id.project_spinner);
        List<String> projects = new ArrayList<String>();
        projects.add("No Associated Project");
        //TODO: Populate projects with options for dropdown
        String[] dropdownArray = projects.toArray(new String[0]);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, dropdownArray);
        projectSpinner.setAdapter(adapter);

    }

    protected void startPomodoro(View view) {
        Intent intent = new Intent(getApplicationContext(), StartPomodoroStep2Activity.class);
        startActivity(intent);
    }
}
