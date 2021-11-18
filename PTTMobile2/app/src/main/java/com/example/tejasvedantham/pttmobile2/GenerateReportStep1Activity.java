package com.example.tejasvedantham.pttmobile2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TimePicker;

import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;

public class GenerateReportStep1Activity extends AppCompatActivity {

    public Spinner projectSpinner;
    public TimePicker startTime;
    public TimePicker endTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.generate_report_1);

        startTime = (TimePicker) findViewById(R.id.startPicker);
        endTime = (TimePicker) findViewById(R.id.endPicker);
        startTime.setIs24HourView(true);
        endTime.setIs24HourView(true);

        projectSpinner = (Spinner) findViewById(R.id.project_report_spinner);
        List<String> projects = new ArrayList<String>();
        //TODO: Populate projects with options for dropdown
        String[] dropdownArray = projects.toArray(new String[0]);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, dropdownArray);
        projectSpinner.setAdapter(adapter);
    }

    public void generateReport(View view) {

        int startHour = startTime.getCurrentHour();
        int startMinute = startTime.getCurrentMinute();
        int endHour = endTime.getCurrentHour();
        int endMinute = endTime.getCurrentMinute();

        //TODO: Query backend for projects in this timeframe

        Intent intent = new Intent(getApplicationContext(), GenerateReportStep2Activity.class);
        startActivity(intent);
    }
}
