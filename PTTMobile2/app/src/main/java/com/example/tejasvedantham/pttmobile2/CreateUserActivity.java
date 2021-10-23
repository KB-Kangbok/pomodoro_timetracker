package com.example.tejasvedantham.pttmobile2;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
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
        JSONObject postData = new JSONObject();
        try {
            postData.put("firstName", firstNameField.getText().toString());
            postData.put("lastName", lastNameField.getText().toString());
            postData.put("email", emailField.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        backendConnections.ExecuteHTTPRequest("/users", Request.Method.POST, postData, new BackendConnections.VolleyCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                Log.d(LOG_TAG,"POST /users RES " + response);
            }

            @Override
            public void onError(VolleyError error) {
                Log.d(LOG_TAG, "POST /users REQ FAILED");
            }
        });

    }

}
