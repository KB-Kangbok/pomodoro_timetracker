package com.example.tejasvedantham.pttmobile2;

import android.content.DialogInterface;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.format.DateFormat;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import static androidx.test.InstrumentationRegistry.getContext;

public class StartPomodoroStep2Activity extends AppCompatActivity {

    public TextView numPomodorosText;
    public int numPomodoros = 1;
    public CountDownTimer timer;
    public TextView timerText;
    public TextView projectNameTextView;
    private String userId = "";
    private String projectId = "";
    private List<Project> projectList;
    private String projectName = "";
    private String startTime = "";
    private String endTime = "";
    private static final String LOG_TAG = StartPomodoroStep2Activity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_pomodoro_2);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            userId = extras.getString("id");
            projectId = extras.getString("projectId");
            projectName = extras.getString("projectName");
            projectList = (List<Project>) getIntent().getSerializableExtra("projects");

        }

        timerText = (TextView) findViewById(R.id.timerText);
        numPomodorosText = (TextView) findViewById(R.id.numPomodorosText);
        numPomodorosText.setText("Pomodoros in this session: " + numPomodoros);

        projectNameTextView =  (TextView) findViewById(R.id.projectNameText);
        projectNameTextView.setText(projectName);
        numPomodorosText.setText("Pomodoros in this session: " + numPomodoros);


        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
        startTime = dateFormat.format(new Date()) + "Z";



    }

    public void startPomodoro(View view) {
        Button b = (Button) findViewById(R.id.startPomodoroButton);
        b.setClickable(false);
        b.setAlpha(.4f);
        numPomodorosText.setText("Pomodoros in this session: " + numPomodoros);
        timer = new CountDownTimer(1800000, 1000) {
//        timer = new CountDownTimer(1800, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long minutes = (millisUntilFinished / 1000) / 60;
                long seconds = (int) ((millisUntilFinished / 1000) % 60);
                timerText.setText("Time Remaining: " + minutes + " : " + seconds);
            }

            @Override
            public void onFinish() {
                timerText.setText("Time's up!");
                AlertDialog.Builder builder = new AlertDialog.Builder(StartPomodoroStep2Activity.this)
                        .setTitle("New Pomodoro")
                        .setMessage("Would you like to start another Pomodoro?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                numPomodoros++;
                                startPomodoro(view);
                                //Start new pomodoro
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                if (projectId != "-1") {
                                    logSession();
                                }

                                Intent intent = new Intent(getApplicationContext(), UserHomeActivity.class);
                                intent.putExtra("id", userId);
                                intent.putExtra("projects", (Serializable) projectList);
                                startActivity(intent);
                            }
                        })
                        .setCancelable(false)
                        .setIcon(android.R.drawable.ic_dialog_alert);

                builder.show();

            }
        }.start();


    }
    public void logSession() {
        Log.d(LOG_TAG, "LOGGING");
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
        endTime = dateFormat.format(new Date()) + "Z";
        Log.d(LOG_TAG, startTime);
        Log.d(LOG_TAG, endTime);
        String url = BackendConnections.baseUrl + String.format("/users/%s/projects/%s/sessions", userId, projectId);
        JSONObject postData = new JSONObject();
        try {
            postData.put("startTime", startTime);
            postData.put("endTime", endTime);
            postData.put("counter", Integer.toString(numPomodoros));


        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d(LOG_TAG, "create user: " + postData.toString());
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, postData, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                Log.d(LOG_TAG, "POST /projects/sessions RES " + response);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(LOG_TAG, "POST /projects/sessions REQ FAILED");
            }
        });
        requestQueue.add(jsonObjectRequest);
    }
    protected void stopPomodoro(View view) {
        timer.cancel();

        AlertDialog.Builder builder = new AlertDialog.Builder(StartPomodoroStep2Activity.this)
                .setTitle("Stop Pomodoro")
                .setMessage("Would you like to log this partial Pomodoro to this Project?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //TODO: Log partial time
                        startActivity(new Intent(getApplicationContext(), UserHomeActivity.class));
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //TODO: Log partial time
                        startActivity(new Intent(getApplicationContext(), UserHomeActivity.class));
                    }
                })
                .setCancelable(false)
                .setIcon(android.R.drawable.ic_dialog_alert);

        builder.show();

        Intent intent = new Intent(getApplicationContext(), UserHomeActivity.class);
        intent.putExtra("id", userId);
        intent.putExtra("projects", (Serializable) projectList);
        startActivity(intent);
    }
}
