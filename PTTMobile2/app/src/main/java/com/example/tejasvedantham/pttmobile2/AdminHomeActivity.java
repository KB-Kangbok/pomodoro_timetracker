package com.example.tejasvedantham.pttmobile2;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AdminHomeActivity extends AppCompatActivity {
    private static final String LOG_TAG = AdminHomeActivity.class.getSimpleName();
    private BackendConnections backendConnections;

    private ListView userListView;
    private ArrayList<User> userList;

    private Button createUserPageButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);

        createUserPageButton = (Button) findViewById(R.id.createUserButton);
        createUserPageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AdminHomeActivity.this, CreateUserActivity.class));
            }
        });

        userListView = (ListView) findViewById(R.id.admin_user_list);
        ArrayList<User> allUsers = new ArrayList<User>();
        UserListAdapter adapter = new UserListAdapter(this, allUsers);

        backendConnections = new BackendConnections(this);
        backendConnections.addHeader("Authorization", "EMPTY FOR NOW");

        // Fetch list of user to generate list
        backendConnections.ExecuteHTTPRequest("/users", Request.Method.GET, null, new BackendConnections.VolleyCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                Log.d(LOG_TAG, "GET /users RES " + response);

                try {
                    JSONArray jsonArray = new JSONArray(response);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String email = jsonObject.getString("email");
                        String firstName = jsonObject.getString("firstName");
                        String lastName = jsonObject.getString("lastName");

                        User user = new User(firstName, lastName, email);
                        userList.add(user);

                    }

                    UserListAdapter adapter = new UserListAdapter(AdminHomeActivity.this, userList);
                    userListView.setAdapter(adapter);

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

    public void DeleteUserClick(String userId){
        // check if user has any project


        backendConnections.ExecuteHTTPRequest("/users/" + userId + "/projects", Request.Method.GET, null, new BackendConnections.VolleyCallback() {
            @Override
            public void onSuccess(JSONObject response) {

                Log.d(LOG_TAG,"GET /users/"+ userId + "/projects" + ",RES " + response);

                try {
                    if(response.getString("projectname") != null){
                        //TODO: show confirm dialog, the button onclick triggers DeleteUserConfirm
                    } else {
                        DeleteUserConfirm(userId);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onError(VolleyError error) {
                //TODO: case when user was already deleted
                Log.d(LOG_TAG, "GET /users/"+ userId + "/projects" + ",REQ FAILED");
            }
        });





    }

    public void DeleteUserConfirm(String userId){
        backendConnections.ExecuteHTTPRequest("/users/" + userId, Request.Method.DELETE, null, new BackendConnections.VolleyCallback() {
            @Override
            public void onSuccess(JSONObject response) {

                Log.d(LOG_TAG,"DELETE /users/"+ userId + ",RES " + response);
            }

            @Override
            public void onError(VolleyError error) {
                //TODO: case when user was already deleted
                Log.d(LOG_TAG, "DELETE /users/"+ userId + ",REQ FAILED");
            }
        });
    }

}
