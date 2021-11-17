package com.example.tejasvedantham.pttmobile2;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

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
        if ((TextUtils.isEmpty(emailField.getText().toString())) || emailField.getText().toString() == "") {
            Toast toast = Toast.makeText(getBaseContext(), "Please provide an email address", Toast.LENGTH_LONG);
            toast.show();
            return;
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
                if (error.networkResponse.statusCode == 409) {
                    Toast toast = Toast.makeText(getBaseContext(), "Email Already Taken", Toast.LENGTH_LONG);
                    toast.show();
                }
            }
        });
        requestQueue.add(jsonObjectRequest);
    }

}
