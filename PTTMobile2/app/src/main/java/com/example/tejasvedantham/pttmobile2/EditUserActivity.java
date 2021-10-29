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
import com.android.volley.VolleyError;

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

        firstNameEdit = (EditText) findViewById(R.id.userFirstNameEdit);
        lastNameEdit= (EditText) findViewById(R.id.userLastNameEdit);
        emailEdit = (TextView) findViewById(R.id.userEmailEdit);

        Bundle extras = getIntent().getExtras();
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

        backendConnections.ExecuteHTTPRequest("/users", Request.Method.GET, null, new BackendConnections.VolleyCallback() {
            @Override
            public void onSuccess(JSONObject response) throws JSONException {
                Log.d(LOG_TAG, String.format("GET %s RES %s", "/users", response));
                // TODO: filter the list of all users using the email specified by this user
                JSONArray users = response.getJSONArray("users");
                for (int i = 0; i < users.length(); ++i) {
                    JSONObject user = (JSONObject) users.get(i);
                    if (user.get("email").equals(emailEdit.getText().toString())) {
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
            postData.put("firstName", firstNameEdit.getText().toString());
            postData.put("lastName", lastNameEdit.getText().toString());
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

        Intent intent = new Intent(getApplicationContext(), AdminHomeActivity.class);
        startActivity(intent);

    }

}
