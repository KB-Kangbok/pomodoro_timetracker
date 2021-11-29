package com.example.tejasvedantham.pttmobile2;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TimePicker;

import java.io.Serializable;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class GenerateReportStep1Activity extends AppCompatActivity {

    private static final String LOG_TAG = UserHomeActivity.class.getSimpleName();

    public TimePicker startTime;
    public TimePicker endTime;
    public CheckBox includeCompletedPomodorosCheckBox;
    public CheckBox includeTotalHoursWorkedOnProjectCheckBox;
    public DatePicker startDatePicker;
    public DatePicker endDatePicker;

    private String userId;
    private Project project;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.generate_report_1);

        startTime = (TimePicker) findViewById(R.id.startPicker);
        endTime = (TimePicker) findViewById(R.id.endPicker);
        includeCompletedPomodorosCheckBox = (CheckBox) findViewById(R.id.completedPomodorosCheckbox);
        includeTotalHoursWorkedOnProjectCheckBox = (CheckBox) findViewById(R.id.totalHoursCheckbox);
        startTime.setIs24HourView(true);
        endTime.setIs24HourView(true);

        startDatePicker = (DatePicker) findViewById(R.id.startDatePicker);
        endDatePicker = (DatePicker) findViewById(R.id.endDatePicker);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            userId = extras.getString("userId");
            project = (Project) getIntent().getSerializableExtra("project");
        }
    }

    public void generateReport(View view) throws JSONException {
        int startHour = startTime.getCurrentHour();
        int startMinute = startTime.getCurrentMinute();
        int endHour = endTime.getCurrentHour();
        int endMinute = endTime.getCurrentMinute();

        int startDay = startDatePicker.getDayOfMonth();
        int startMonth = startDatePicker.getMonth();
        int startYear = startDatePicker.getYear();

        int endDay = endDatePicker.getDayOfMonth();
        int endMonth = endDatePicker.getMonth();
        int endYear = endDatePicker.getYear();

        String startTimerFormat;
        String endTimeFormat;
        startTimerFormat = String.format("%s-%s-%sT%s:%sZ", startYear, startMonth+1, startDay, startHour, startMinute);
        endTimeFormat = String.format("%s-%s-%sT%s:%sZ", endYear, endMonth+1, endDay, endHour, endMinute);
        Log.d(LOG_TAG, String.format("start time: %s, end time: %s", startTimerFormat, endTimeFormat));

        //TODO: Query backend for projects in this timeframe
        String url = String.format(BackendConnections.baseUrl + "/users/%s/projects/%s/report", userId, project.id);
        String params = String.format("?from=%s&to=%s&includeCompletedPomodoros=%s&includeTotalHoursWorkedOnProject=%s", startTimerFormat, endTimeFormat, includeCompletedPomodorosCheckBox.isChecked(), includeTotalHoursWorkedOnProjectCheckBox.isChecked());
        url = url + params;
        Log.d(LOG_TAG, String.format("url: %s", url));
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d(LOG_TAG, String.format("get response %s", response));
                Intent intent = new Intent(getApplicationContext(), GenerateReportStep2Activity.class);
                try {
                    JSONArray sessions = response.getJSONArray("reportSessionList");
                    List<Session> sessionList = new ArrayList<>();
                    for (int i = 0; i < sessions.length(); ++i) {
                        JSONObject o = sessions.getJSONObject(i);
                        sessionList.add(new Session(o.getString("startingTime"), o.getString("endingTime"), -1, null, o.getDouble("hoursWorked")));
                    }
                    intent.putExtra("sessions", (Serializable) sessionList);
                    if (includeCompletedPomodorosCheckBox.isChecked()) {
                        intent.putExtra("completedPomodoros", response.getInt("completedPomodoros"));
                    }
                    if (includeTotalHoursWorkedOnProjectCheckBox.isChecked()) {
                        intent.putExtra("totalHoursWorkedOnProject", response.getDouble("totalHoursWorkedOnProject"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                intent.putExtra("project", project);
                startActivity(intent);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(LOG_TAG, String.format("get error %s", error));
            }
        });
        requestQueue.add(jsonObjectRequest);
    }
}
