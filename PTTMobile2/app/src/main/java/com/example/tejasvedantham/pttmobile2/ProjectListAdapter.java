package com.example.tejasvedantham.pttmobile2;

import static com.example.tejasvedantham.pttmobile2.LoginActivity.userSession;

import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

public class ProjectListAdapter extends ArrayAdapter<Project> {

    private Context context;
    private String userId;
    private static final String LOG_TAG = ProjectListAdapter.class.getSimpleName();
    public static final String CONFIRM_MSG = "The project has time already logged to it. Do you still want to delete it?";

    public ProjectListAdapter(Context context, ArrayList<Project> data, String userId) {
        super(context, 0, data);
        this.context = context;
        this.userId = userId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Project project = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.project_row_item, parent, false);
        }

        TextView nameProject = (TextView) convertView.findViewById(R.id.nameProject);

        nameProject.setText(project.projectName);

        Button deleteProjectButton = (Button) convertView.findViewById(R.id.deleteProject);
        Button generateReportButton = (Button) convertView.findViewById(R.id.generateReport);

        deleteProjectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(LOG_TAG, "click delete project: " + project.projectName +  " " + project.id);
                deleteProject(project.id);
            }
        });

        generateReportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, GenerateReportStep1Activity.class);
                intent.putExtra("project", project);
                intent.putExtra("userId", userId);
                context.startActivity(intent);
            }
        });

        return convertView;
    }

    public void deleteProject(String projectId) {
        String url = BackendConnections.baseUrl + String.format("/users/%s/projects/%s/sessions", userId, projectId);
        Log.d(LOG_TAG, "delete check project sessions url: " + url);

        RequestQueue requestQueue = Volley.newRequestQueue(getContext());

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                if (response.length() > 0) {
//                    Utils.displayExceptionMessage(getContext(), CONFIRM_MSG);
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    DialogInterface.OnClickListener listener  = (dialog, confirm) -> {
                        switch (confirm){
                            case DialogInterface.BUTTON_POSITIVE:
                                deleteProjectConfirm(projectId);
                                break;
                        }
                    };
                    builder.setMessage(CONFIRM_MSG);
                    builder.setPositiveButton("Yes", listener);
                    builder.setNegativeButton("No", listener);
                    builder.show();
                } else {
                    deleteProjectConfirm(projectId);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(LOG_TAG, String.format("GET %s FAILED", url));
            }
        });
        requestQueue.add(jsonArrayRequest);
    }

    private void deleteProjectConfirm(String projectId) {
        String url = BackendConnections.baseUrl + String.format("/users/%s/projects/%s", userId, projectId);
        Log.d(LOG_TAG, "delete project url: " + url);

        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.DELETE, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d(LOG_TAG, String.format("DELETE %s RES %s", url, response.toString()));
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(LOG_TAG, String.format("DELETE %s FAILED", url));
            }
        });
        requestQueue.add(jsonObjectRequest);

        Intent intent = new Intent(context.getApplicationContext(), UserHomeActivity.class);
        intent.putExtra("id", userId);
        context.startActivity(intent);
    }
}
