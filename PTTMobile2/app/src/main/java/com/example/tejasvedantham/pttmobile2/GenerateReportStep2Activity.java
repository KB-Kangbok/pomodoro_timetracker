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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.generate_report_2);

        projectName = (TextView) findViewById(R.id.projectNameText);
        information = (TextView) findViewById(R.id.informationText);

        //TODO: Append information to report here
        projectName.append("Project Name HERE");
        information.append("Information HERR");

    }


}
