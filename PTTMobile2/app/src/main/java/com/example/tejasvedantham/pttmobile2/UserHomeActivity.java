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
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class UserHomeActivity extends AppCompatActivity {
    private static final String LOG_TAG = UserHomeActivity.class.getSimpleName();

    private ListView projectListView;
    private ArrayList<Project> projectList = new ArrayList<>();

    private Button createProjectPageButton;

    private BackendConnections backendConnections;
    private String userId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activitiy_user_home);

        backendConnections = new BackendConnections(this);
        backendConnections.addHeader("Authorization", "EMPTY FOR NOW");

        projectListView = (ListView) findViewById(R.id.project_list);
        ArrayList<Project> projectList = new ArrayList<>();
        ProjectListAdapter adapter = new ProjectListAdapter(this, projectList, userId);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            userId = extras.getString("id");
        }

        //TODO: Add projects to projectList and update adapter
        createProjectPageButton = (Button) findViewById(R.id.createProject);
        createProjectPageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), CreateProjectActivity.class);
                intent.putExtra("id", userSession.getUserId());
                startActivity(intent);
//                startActivity(new Intent(UserHomeActivity.this, CreateProjectActivity.class));
            }
        });

        populateProjectList();
    }

    public void populateProjectList(){
        projectListView = (ListView) findViewById(R.id.project_list);
        Log.d("user", userId);
        // Fetch list of user to generate list
        String url = String.format(BackendConnections.baseUrl + "/users/%s/projects", userId);
        Log.d("user", userId);
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET,url,null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray jsonArray) {

                        try {
                            for (int i = 0; i < jsonArray.length(); i++) { ;
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                String projectname = jsonObject.getString("projectname");
                                String id = jsonObject.getString("id");

                                Project project = new Project(projectname, id);
                                projectList.add(project);

                            }

                            ProjectListAdapter adapter = new ProjectListAdapter(UserHomeActivity.this, projectList, userId);
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
        requestQueue.add(jsonArrayRequest);
    }

    public void createPomodoro(View view) {
        Intent intent = new Intent(getApplicationContext(), StartPomodoroStep1Activity.class);
        startActivity(intent);
    }
}
