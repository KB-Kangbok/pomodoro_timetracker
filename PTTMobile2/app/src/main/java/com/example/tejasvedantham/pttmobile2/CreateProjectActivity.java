package com.example.tejasvedantham.pttmobile2;

import static com.example.tejasvedantham.pttmobile2.LoginActivity.userSession;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

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

        String url = String.format("http://172.16.33.67:8080" + urlFormat, userId);
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, postData,
        new Response.Listener< JSONObject >() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d(LOG_TAG, String.format("POST %s RES %s", url, response));
                startActivity(new Intent(getApplicationContext(), UserHomeActivity.class));
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("namee", error.toString());
                error.printStackTrace();

            }
        });
        requestQueue.add(request);
//    }

//        backendConnections.ExecuteHTTPRequest(url, Request.Method.POST, postData, new BackendConnections.VolleyCallback() {
//
//            @Override
//            public void onSuccess(JSONObject response) {
//                Log.d(LOG_TAG, String.format("POST %s RES %s", url, response));
//            }

//            @Override
//            public void onError(VolleyError error) {
//                Log.d(LOG_TAG, String.format("POST %s REQ FAILED", url));
//            }
//        });
    }
}
