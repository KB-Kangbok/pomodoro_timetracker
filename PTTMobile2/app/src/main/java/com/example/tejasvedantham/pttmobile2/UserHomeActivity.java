package com.example.tejasvedantham.pttmobile2;

import static com.example.tejasvedantham.pttmobile2.LoginActivity.userSession;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class UserHomeActivity extends AppCompatActivity {
    private static final String LOG_TAG = UserHomeActivity.class.getSimpleName();
    private static final String CONFIRM_MSG = "The project has time already logged to it. Do you still want to delete it?";

    private ListView projectListView;
    private ArrayList<Project> projectList = new ArrayList<>();

    private Button createProjectPageButton;

    private BackendConnections backendConnections;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activitiy_user_home);

        backendConnections = new BackendConnections(this);
        backendConnections.addHeader("Authorization", "EMPTY FOR NOW");

        projectListView = (ListView) findViewById(R.id.project_list);
        ArrayList<Project> projectList = new ArrayList<>();
        ProjectListAdapter adapter = new ProjectListAdapter(this, projectList);

        //TODO: Add projects to projectList and update adapter
        createProjectPageButton = (Button) findViewById(R.id.createProject);
        createProjectPageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(UserHomeActivity.this, CreateProjectActivity.class));
            }
        });

        projectListView = (ListView) findViewById(R.id.project_list);
        Log.d("user", userSession.getUserId());
        // Fetch list of user to generate list
        String url = String.format("http://172.16.33.67:8080/users/%s/projects", userSession.getUserId());
        Log.d("user", userSession.getUserId());
        RequestQueue requestQueue = Volley.newRequestQueue(this);
//        String url="http://172.16.33.67:8080" +"/users";
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET,url,null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray jsonArray) {

                        try {
                            for (int i = 0; i < jsonArray.length(); i++) { ;
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            String projectname = jsonObject.getString("projectname");

                            Project project = new Project(projectname);
                            projectList.add(project);

                        }

                        ProjectListAdapter adapter = new ProjectListAdapter(UserHomeActivity.this, projectList);
                        projectListView.setAdapter(adapter);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },  new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        jsonArrayRequest.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 5000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 5000;
            }

            @Override
            public void retry(VolleyError error) throws VolleyError {

            }
        });
        requestQueue.add(jsonArrayRequest);}
//        backendConnections.ExecuteHTTPRequest(url, Request.Method.GET, null, new BackendConnections.VolleyCallback() {
//            @Override
//            public void onSuccess(JSONObject response) {
//                Log.d(LOG_TAG, String.format("GET $s RES %s", url, response));
//
//                try {
//                    JSONArray jsonArray = new JSONArray(response);
//                    for (int i = 0; i < jsonArray.length(); i++) {
//                        JSONObject jsonObject = jsonArray.getJSONObject(i);
//                        String projectname = jsonObject.getString("projectname");
//
//                        Project project = new Project(projectname);
//                        projectList.add(project);
//
//                    }
//
//                    ProjectListAdapter adapter = new ProjectListAdapter(UserHomeActivity.this, projectList);
//                    projectListView.setAdapter(adapter);
//
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//
//            }
//
//            @Override
//            public void onError(VolleyError error) {
//                Log.d(LOG_TAG, String.format("GET %s REQ FAILED", error.toString()));
//            }
//        });
//    }


    public void deleteProject(View view, String projectId) {
        String url = String.format("/users/%s/projects/%s/sessions", userSession.getUserId(), projectId);
        backendConnections.ExecuteHTTPRequest(url, Request.Method.GET, null, new BackendConnections.VolleyCallback() {
            @Override
            public void onSuccess(JSONObject response) throws JSONException {
                Log.d(LOG_TAG, String.format("GET %s RES %s", url, response.toString()));
                JSONArray sessions = new JSONArray(response);
                if (sessions.length() > 0) {
                    Utils.displayExceptionMessage(getApplicationContext(), CONFIRM_MSG);
                } else {
                    deleteProjectConfirm(projectId);
                }
            }

            @Override
            public void onError(VolleyError error) {
                Log.d(LOG_TAG, String.format("GET %s FAILED", url));
            }
        });
    }

    private void deleteProjectConfirm(String projectId) {
        String url = String.format("/users/%s/projects/%s", userSession.getUserId(), projectId);
        backendConnections.ExecuteHTTPRequest(url, Request.Method.DELETE, null, new BackendConnections.VolleyCallback() {
            @Override
            public void onSuccess(JSONObject response) throws JSONException {
                Log.d(LOG_TAG, String.format("DELETE %s RES %s", url, response.toString()));
            }

            @Override
            public void onError(VolleyError error) {
                Log.d(LOG_TAG, String.format("DELETE %s FAILED", url));
            }
        });
    }
}
