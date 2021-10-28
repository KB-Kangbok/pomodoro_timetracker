package com.example.tejasvedantham.pttmobile2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
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
        String url = "/users";
        String email = usernameField.getText().toString();

        // “admin" was specified in use cases
        if (email.equals("admin")) {
            startActivity(new Intent(getApplicationContext(), AdminHomeActivity.class));
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, "http://10.2.130.50:8080/users",
                null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Utils.displayExceptionMessage(getApplicationContext(), response.toString());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(LOG_TAG, String.format("GET %s REQ FAILED", url));
            }
        });
        queue.add(request);


//        backendConnections.ExecuteHTTPRequest(url, Request.Method.GET, null, new BackendConnections.VolleyCallback() {
//            @Override
//            public void onSuccess(JSONObject response) throws JSONException {
//                Log.d(LOG_TAG, String.format("GET %s RES %s", url, response));
//                // TODO: filter the list of all users using the email specified by this user
//                JSONArray users = response.getJSONArray("users");
//                for (int i = 0; i < users.length(); ++i) {
//                    JSONObject user = (JSONObject) users.get(i);
//                    if (user.get("email").equals(email)) {
//                        userSession.setUserId(user.get("id").toString());
//
//                        startActivity(new Intent(getApplicationContext(), UserHomeActivity.class));
//                    }
//                }
//                Utils.displayExceptionMessage(getApplicationContext(), "Invalid email");
//            }
//
//            @Override
//            public void onError(VolleyError error) {
//                Log.d(LOG_TAG, String.format("GET %s REQ FAILED", url));
//            }
//        });
    }
}