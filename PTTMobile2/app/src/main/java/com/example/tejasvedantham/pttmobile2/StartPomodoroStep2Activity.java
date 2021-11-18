package com.example.tejasvedantham.pttmobile2;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import static androidx.test.InstrumentationRegistry.getContext;

public class StartPomodoroStep2Activity extends AppCompatActivity {

    public TextView numPomodorosText;
    public int numPomodoros = 1;
    public CountDownTimer timer;
    public TextView timerText;
    private String userId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_pomodoro_2);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            userId = extras.getString("id");

        }

        timerText = (TextView) findViewById(R.id.timerText);
        numPomodorosText = (TextView) findViewById(R.id.numPomodorosText);
        numPomodorosText.setText("Pomodoros in this session: " + numPomodoros);

        startPomodoro();

    }

    public void startPomodoro() {
        numPomodorosText.setText("Pomodoros in this session: " + numPomodoros);
//        timer = new CountDownTimer(1800000, 1000) {
        timer = new CountDownTimer(1800, 1000) {
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
                                startPomodoro();
                                //Start new pomodoro
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //TODO: System should log details to backend
                                Intent intent = new Intent(getApplicationContext(), UserHomeActivity.class);
                                intent.putExtra("id", userId);
                                startActivity(intent);
                            }
                        })
                        .setCancelable(false)
                        .setIcon(android.R.drawable.ic_dialog_alert);

                builder.show();

            }
        }.start();

    }

    protected void stopPomodoro(View view) {
        timer.cancel();

        AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext())
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

        builder.create().show();

        Intent intent = new Intent(getApplicationContext(), UserHomeActivity.class);
        startActivity(intent);
    }
}
