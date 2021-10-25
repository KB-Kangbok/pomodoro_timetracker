package com.example.tejasvedantham.pttmobile2;

import static com.example.tejasvedantham.pttmobile2.LoginActivity.userSession;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

public class CreateProjectActivity extends AppCompatActivity {

    private static final String LOG_TAG = CreateProjectActivity.class.getSimpleName();
    private BackendConnections backendConnections;

    private String userId; // FIXME: how to get user id?
    private EditText projectNameField;
    private String urlFormat = "/users/%s/projects"; // /users/{userId}/projects

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_project);

        projectNameField = (EditText) findViewById(R.id.projectName);

        userId = userSession.getUserId();
        backendConnections = new BackendConnections(this);
        backendConnections.addHeader("Authorization", "EMPTY FOR NOW");
    }

    public void createProject(View view) {
        JSONObject postData = new JSONObject();
        try {
            postData.put("projectname", projectNameField.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String url = String.format(urlFormat, userId);

        backendConnections.ExecuteHTTPRequest(url, Request.Method.POST, postData, new BackendConnections.VolleyCallback() {

            @Override
            public void onSuccess(JSONObject response) {
                Log.d(LOG_TAG, String.format("POST %s RES %s", url, response));
            }

            @Override
            public void onError(VolleyError error) {
                Log.d(LOG_TAG, String.format("POST %s REQ FAILED", url));
            }
        });
    }
}
