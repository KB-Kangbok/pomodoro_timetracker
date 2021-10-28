package com.example.tejasvedantham.pttmobile2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.RetryPolicy;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    private static final String LOG_TAG = LoginActivity.class.getSimpleName();
    public static SessionManager userSession;
    private BackendConnections backendConnections;

    RequestQueue queue;

    public Button loginButton;
    public EditText usernameField;
    public EditText passwordField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginButton = (Button) findViewById(R.id.loginButton);
        usernameField = (EditText) findViewById(R.id.usernameField);
//        passwordField = (EditText) findViewById(R.id.passwordField);

        userSession = new SessionManager(getApplicationContext());
        backendConnections = new BackendConnections(this);

        queue = Volley.newRequestQueue(this);
    }

    public void login(View view) {
        // TODO: Login logic here
//        String url = "/users";
        String email = usernameField.getText().toString();

        // “admin" was specified in use cases
        if (email.equals("admin")) {
            startActivity(new Intent(getApplicationContext(), AdminHomeActivity.class));
            return;
        }
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String url = "http://localhost:8080" + "/users";
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray jsonArray) {
                        try {
                            JSONArray users = jsonArray;
                            boolean userFound = false;
                            for (int i = 0; i < users.length(); ++i) {
                                JSONObject user = (JSONObject) users.get(i);
                                if (user.get("email").equals(email)) {
                                    userSession.setUserId(user.get("id").toString());
                                    userFound = true;
                                    startActivity(new Intent(getApplicationContext(), UserHomeActivity.class));
                                }
                            }
                            if (!userFound) {
                                Log.d("NO USER", "no user");
                                Toast toast = Toast.makeText(getBaseContext(), "No such user", Toast.LENGTH_LONG);
                                toast.setGravity(Gravity.CENTER, 0, 0);
                                toast.show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        jsonArrayRequest.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 5000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 5000;
            }

            @Override
            public void retry(VolleyError error) throws VolleyError {

            }
        });
        requestQueue.add(jsonArrayRequest);
    }


}