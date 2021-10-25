package com.example.tejasvedantham.pttmobile2;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    private static final String LOG_TAG = LoginActivity.class.getSimpleName();
    public static SessionManager userSession;
    private BackendConnections backendConnections;

    public Button loginButton;
    public EditText usernameField;
    public EditText passwordField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginButton = (Button) findViewById(R.id.loginButton);
        usernameField = (EditText) findViewById(R.id.usernameField);
        passwordField = (EditText) findViewById(R.id.passwordField);

        userSession = new SessionManager(getApplicationContext());
        backendConnections = new BackendConnections(this);
    }

    public void login(View view) {
        // TODO: Login logic here

//        userSession.setUserId(userId);
    }
}