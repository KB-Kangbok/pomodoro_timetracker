package com.example.tejasvedantham.pttmobile2;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class CreateUserActivity extends AppCompatActivity {

    private static final String LOG_TAG = CreateUserActivity.class.getSimpleName();
    private BackendConnections backendConnections;

    private EditText firstNameField;
    private EditText lastNameField;
    private EditText emailField;

    private RequestQueue mQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_user);

        firstNameField = (EditText) findViewById(R.id.userFirstName);
        lastNameField = (EditText) findViewById(R.id.userLastName);
        emailField = (EditText) findViewById(R.id.userEmail);

        backendConnections = new BackendConnections(this);
        backendConnections.addHeader("Authorization", "EMPTY FOR NOW");
    }

    public void createUser(View view) {
        Bundle extras = getIntent().getExtras();
        if(extras != null){
            EditUser(view);
        }


        JSONObject postData = new JSONObject();
        try {
            postData.put("firstName", firstNameField.getText().toString());
            postData.put("lastName", lastNameField.getText().toString());
            postData.put("email", emailField.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d(LOG_TAG, "create user: " + postData.toString());

        String url = BackendConnections.baseUrl + "/users";
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, postData, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d(LOG_TAG, "POST /users RES " + response);
                startActivity(new Intent(getApplicationContext(), AdminHomeActivity.class));
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(LOG_TAG, "POST /users REQ FAILED");
            }
        });
        requestQueue.add(jsonObjectRequest);
    }

    String userId = "";

    public void EditUser(View view){
        backendConnections.ExecuteHTTPRequest("/users", Request.Method.GET, null, new BackendConnections.VolleyCallback() {
            @Override
            public void onSuccess(JSONObject response) throws JSONException {
                Log.d(LOG_TAG, String.format("GET %s RES %s", "/users", response));
                // TODO: filter the list of all users using the email specified by this user
                JSONArray users = response.getJSONArray("users");
                for (int i = 0; i < users.length(); ++i) {
                    JSONObject user = (JSONObject) users.get(i);
                    if (user.get("email").equals(emailField.getText().toString())) {
                        userId = "" + user.get("Id").toString();
                    }
                }
            }

            @Override
            public void onError(VolleyError error) {
                Log.d(LOG_TAG, String.format("GET %s REQ FAILED", "/users"));
            }
        });

        JSONObject postData = new JSONObject();
        try {
            postData.put("firstName", firstNameField.getText().toString());
            postData.put("lastName", lastNameField.getText().toString());
            postData.put("email", emailField.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        backendConnections.ExecuteHTTPRequest("/users/" + userId, Request.Method.PUT, postData, new BackendConnections.VolleyCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                //TODO: show success message

                Log.d(LOG_TAG,"PUT /users/" + userId + ", RES " + response);

            }

            @Override
            public void onError(VolleyError error) {
                //TODO: use case when user already exists, an error dialog
                Log.d(LOG_TAG, "PUT /users/" + userId + ", REQ FAILED");
            }
        });
    }


}
