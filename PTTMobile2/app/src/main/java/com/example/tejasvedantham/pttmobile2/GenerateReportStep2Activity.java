package com.example.tejasvedantham.pttmobile2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;

public class GenerateReportStep2Activity extends AppCompatActivity {

    public TextView projectName;
    public TextView information;

    private List<Session> sessionList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.generate_report_2);

        projectName = (TextView) findViewById(R.id.projectNameText);
        information = (TextView) findViewById(R.id.informationText);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            Project project = (Project) extras.getSerializable("project");
            projectName.append(project.projectName);

            sessionList = (List<Session>) getIntent().getSerializableExtra("sessions");
            for (Session s : sessionList) {
                information.append("\n");
                information.append(formatSession(s));
                information.append("\n");
            }
        }

        if (extras.containsKey("completedPomodoros")) {
            information.append("\n");
            information.append(formatCompletedPomodoros(extras.getInt("completedPomodoros")));
        }

        if (extras.containsKey("totalHoursWorkedOnProject")) {
            information.append("\n");
            information.append(formatTotalHoursWorkedOnProject(extras.getDouble("totalHoursWorkedOnProject")));
        }

    }

    public static String formatSession(Session session) {
        return String.format("startingTime: %s, endingTime: %s, hoursWorked: %.1f", session.startingTime, session.endingTime, session.hoursWorked);
    }

    public static String formatCompletedPomodoros(int completedPomodoros) {
        return String.format("completedPomodoros: %d", completedPomodoros);
    }

    public static String formatTotalHoursWorkedOnProject(double totalHoursWorkedOnProject) {
        return String.format("totalHoursWorkedOnProject: %.1f", totalHoursWorkedOnProject);
    }
}
