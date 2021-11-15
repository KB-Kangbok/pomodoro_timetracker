package com.example.tejasvedantham.pttmobile2;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

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

public class EditUserActivity extends AppCompatActivity {

    private static final String LOG_TAG = CreateUserActivity.class.getSimpleName();
    private BackendConnections backendConnections;
    private RequestQueue mQueue;

    public EditText firstNameEdit;
    public EditText lastNameEdit;
    public TextView emailEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user);
//        Log.d("edit", getIntent().getExtras().toString());
        firstNameEdit = (EditText) findViewById(R.id.userFirstNameEdit);
        lastNameEdit= (EditText) findViewById(R.id.userLastNameEdit);
        emailEdit = (TextView) findViewById(R.id.userEmailEdit);

        Bundle extras = getIntent().getExtras();
//        Log.d("edit user", extras.getString("firstName"));
        if (extras != null) {
            String firstName = extras.getString("firstName");
            String lastName = extras.getString("lastName");
            String email = extras.getString("email");

            firstNameEdit.setText(firstName);
            lastNameEdit.setText(lastName);
            emailEdit.setText(email);
        }


    }

    String userId = "";

    public void submitEditUser(View view) {

        String newFirstName = firstNameEdit.getText().toString();
        String newLastName = lastNameEdit.getText().toString();

        //TODO: Submit edited user info here using the two variables above

        Bundle extras = getIntent().getExtras();
        userId = extras.getString("userId");

        JSONObject postData = new JSONObject();
        try {
            postData.put("firstName", newFirstName);
            postData.put("lastName", newLastName);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String url = BackendConnections.baseUrl + "/users/" + userId;
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PUT, url, postData, new Response.Listener<JSONObject>() {
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

        Intent intent = new Intent(getApplicationContext(), AdminHomeActivity.class);
        startActivity(intent);

    }

}
