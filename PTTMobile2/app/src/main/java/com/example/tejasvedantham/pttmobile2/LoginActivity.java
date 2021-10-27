package com.example.tejasvedantham.pttmobile2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.VolleyError;

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
        String url = "/users";
        String email = usernameField.getText().toString();

        if (email.equals("administrator")) {
            Intent intent = new Intent(getApplicationContext(), AdminHomeActivity.class);
            startActivity(intent);
        }

        backendConnections.ExecuteHTTPRequest(url, Request.Method.GET, null, new BackendConnections.VolleyCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                Log.d(LOG_TAG, String.format("GET %s RES %s", url, response));
                // TODO: filter the list of all users using the email specified by this user
                // userSession.setUserId(userId);
            }

            @Override
            public void onError(VolleyError error) {
                Log.d(LOG_TAG, String.format("GET %s REQ FAILED", url));
            }
        });
    }
}