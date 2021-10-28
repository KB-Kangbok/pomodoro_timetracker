package com.example.tejasvedantham.pttmobile2;

<<<<<<< HEAD
import static com.example.tejasvedantham.pttmobile2.LoginActivity.userSession;

=======
import android.content.Intent;
>>>>>>> e0f4f029acd9011c220a712ca0173601ff85a286
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class UserHomeActivity extends AppCompatActivity {
    private static final String LOG_TAG = UserHomeActivity.class.getSimpleName();
    private static final String CONFIRM_MSG = "The project has time already logged to it. Do you still want to delete it?";

    private ListView projectListView;
    private ArrayList<Project> projectList;

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
        createProjectPageButton = (Button) findViewById(R.id.createUserButton);
        createProjectPageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(UserHomeActivity.this, CreateUserActivity.class));
            }
        });

        projectListView = (ListView) findViewById(R.id.admin_user_list);

        // Fetch list of user to generate list
        backendConnections.ExecuteHTTPRequest("users/" + "/projects/", Request.Method.GET, null, new BackendConnections.VolleyCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                Log.d(LOG_TAG, "GET /users/" + "/projects RES " + response);

                try {
                    JSONArray jsonArray = new JSONArray(response);
                    for (int i = 0; i < jsonArray.length(); i++) {
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

            @Override
            public void onError(VolleyError error) {
                Log.d(LOG_TAG, "GET /users REQ FAILED");
            }
        });
    }

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
