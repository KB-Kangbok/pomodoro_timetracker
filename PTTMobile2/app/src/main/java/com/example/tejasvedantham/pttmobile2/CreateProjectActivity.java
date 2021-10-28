package com.example.tejasvedantham.pttmobile2;

import static com.example.tejasvedantham.pttmobile2.LoginActivity.userSession;

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

import org.json.JSONException;
import org.json.JSONObject;

public class CreateProjectActivity extends AppCompatActivity {

    private static final String LOG_TAG = CreateProjectActivity.class.getSimpleName();
    private BackendConnections backendConnections;

    private String userId; // FIXME: how to get user id?
    private EditText projectNameField;

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

        Log.d(LOG_TAG, "create project: " + postData.toString());

        String url = BackendConnections.baseUrl + String.format("/users/%s/projects", userId);
        Log.d(LOG_TAG, "to url: " + url);

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, postData, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d(LOG_TAG, String.format("POST %s RES %s", url, response));
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(LOG_TAG, String.format("POST %s REQ FAILED", url));
            }
        });
        requestQueue.add(jsonObjectRequest);

    }
}
