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

    private String userId;
    private Project project;
//    private List<Project> projectList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.generate_report_1);

        startTime = (TimePicker) findViewById(R.id.startPicker);
        endTime = (TimePicker) findViewById(R.id.endPicker);
        startTime.setIs24HourView(true);
        endTime.setIs24HourView(true);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            userId = extras.getString("userId");
            project = (Project) getIntent().getSerializableExtra("project");
//            projectList = (List<Project>) getIntent().getSerializableExtra("userId");
        }

//        projectSpinner = (Spinner) findViewById(R.id.project_report_spinner);
//        ArrayAdapter<Project> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, projectList);
//        projectSpinner.setAdapter(adapter);
    }

    public void generateReport(View view) throws JSONException {
        int startHour = startTime.getCurrentHour();
        int startMinute = startTime.getCurrentMinute();
        int endHour = endTime.getCurrentHour();
        int endMinute = endTime.getCurrentMinute();
        String startTimerFormat;
        String endTimeFormat;
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        startTimerFormat = formatter.format(date) + "T" + startHour + ":" + startMinute + "Z";
        endTimeFormat = formatter.format(date) + "T" + endHour + ":" + endMinute + "Z";
        Log.d(LOG_TAG, String.format("start time: %s, end time: %s", startTimerFormat, endTimeFormat));

        // TODO: need these two options on the UI
        boolean completedPomodoros = true;
        boolean totalHoursWorkedOnProject = true;

        //TODO: Query backend for projects in this timeframe
//        String projectId = projectList.get(projectSpinner.getSelectedItemPosition()).id;
        String url = String.format(BackendConnections.baseUrl + "/users/%s/projects/%s/report", userId, project.id);
        String params = String.format("?from=%s&to=%s&completedPomodoros=%s&totalHoursWorkedOnProject=%s", startTimerFormat, endTimeFormat, completedPomodoros, totalHoursWorkedOnProject);
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
                        sessionList.add(new Session(o.getString("startingTime"), o.getString("endingTime"), o.getInt("hoursWorked")));
                    }
                    intent.putExtra("sessions", (Serializable) sessionList);
                    if (response.has("completedPomodoros")) {
                        intent.putExtra("completedPomodoros", response.getString("completedPomodoros"));
                    }
                    if (response.has("totalHoursWorkedOnProject")) {
                        intent.putExtra("totalHoursWorkedOnProject", response.getString("totalHoursWorkedOnProject"));
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

    public void onCheckboxClicked(View view) {
        boolean checked = ((CheckBox) view).isChecked();

        switch (view.getId()) {

            case R.id.completedPomodorosCheckbox:
                if (checked) {
                    //TODO: If completed Pomodoros is checked
                } else {
                    //TODO: If completed Pomodoros is NOT checked
                }
                break;

            case R.id.totalHoursCheckbox:
                if (checked) {
                    //TODO: If total hours worked on project is checked
                }
                else {
                    //TODO: If total hours worked on project is NOT checked
                }
                break;
        }
    }
}
