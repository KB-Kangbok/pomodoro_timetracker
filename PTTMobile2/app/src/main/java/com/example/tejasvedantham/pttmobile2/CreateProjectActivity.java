package com.example.tejasvedantham.pttmobile2;

import static com.example.tejasvedantham.pttmobile2.LoginActivity.userSession;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_project);


        projectNameField = (EditText) findViewById(R.id.projectName);

//        userId = userSession.getUserId();
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            userId = extras.getString("id");
        }
    }

    public void createProject(View view) {
        if ((TextUtils.isEmpty(projectNameField.getText().toString())) || projectNameField.getText().toString() == "") {
            Toast toast = Toast.makeText(getBaseContext(), "Please provide a name for the project", Toast.LENGTH_LONG);
            toast.show();
            return;
        }
        Project project = new Project(projectNameField.getText().toString(), null);
        createProjectInternal(project, userId, this, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d(LOG_TAG, String.format("POST create project RES %s", response));
                Intent intent = new Intent(getApplicationContext(), UserHomeActivity.class);
                intent.putExtra("id", userId);
                startActivity(intent);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(LOG_TAG, String.format("POST create project REQ FAILED"));
                if (error.networkResponse.statusCode == 409) {
                    Toast toast = Toast.makeText(getBaseContext(), "Project Name Already Taken", Toast.LENGTH_LONG);
                    toast.show();
                }
            }
        });
    }

    public static void createProjectInternal(Project project, String userId, Context context, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
        JSONObject postData = new JSONObject();
        try {
            postData.put("projectname", project.projectName);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d(LOG_TAG, "create project: " + postData.toString());
        String url = BackendConnections.baseUrl + String.format("/users/%s/projects", userId);
        Log.d(LOG_TAG, "to url: " + url);

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, postData, listener, errorListener);
        requestQueue.add(jsonObjectRequest);
    }
}
