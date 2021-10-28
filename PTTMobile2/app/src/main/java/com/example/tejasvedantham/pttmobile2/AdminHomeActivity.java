package com.example.tejasvedantham.pttmobile2;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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

import java.util.ArrayList;
import java.util.List;

public class AdminHomeActivity extends AppCompatActivity {
    private static final String LOG_TAG = AdminHomeActivity.class.getSimpleName();
    private BackendConnections backendConnections;

    private ListView userListView;
    private ArrayList<User> userList = new ArrayList<>();

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

        backendConnections = new BackendConnections(this);
        backendConnections.addHeader("Authorization", "EMPTY FOR NOW");

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String url="http://172.16.33.67:8080" +"/users";
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET,url,null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray jsonArray) {

                        try {
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

        // Fetch list of user to generate list
//        backendConnections.ExecuteHTTPRequest("/users", Request.Method.GET, null, new BackendConnections.VolleyCallback() {
//            @Override
//            public void onSuccess(JSONObject response) {
//                Log.d(LOG_TAG, "GET /users RES " + response);
//
//                try {
//                    JSONArray jsonArray = new JSONArray(response);
//                    for (int i = 0; i < jsonArray.length(); i++) {
//                        JSONObject jsonObject = jsonArray.getJSONObject(i);
//                        String email = jsonObject.getString("email");
//                        String firstName = jsonObject.getString("firstName");
//                        String lastName = jsonObject.getString("lastName");
//
//                        User user = new User(firstName, lastName, email);
//                        userList.add(user);
//
//                    }
//
//                    UserListAdapter adapter = new UserListAdapter(AdminHomeActivity.this, userList);
//                    userListView.setAdapter(adapter);
//
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//
//            }
//
//            @Override
//            public void onError(VolleyError error) {
//                Log.d(LOG_TAG, "GET /users REQ FAILED");
//            }
//        });
    }

    public void deleteUser(View view, String userId){
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

    public void editUser(View view) {

        RelativeLayout parentRow = (RelativeLayout) view.getParent();

        String firstName = ((TextView) parentRow.findViewById(R.id.firstNameText)).getText().toString();
        String lastName = ((TextView) parentRow.findViewById(R.id.lastNameText)).getText().toString();;
        String email = ((TextView) parentRow.findViewById(R.id.emailText)).getText().toString();;

        Intent intent = new Intent(getApplicationContext(), CreateUserActivity.class);
        intent.putExtra("firstName", firstName);
        intent.putExtra("lastName", lastName);
        intent.putExtra("email", email);

        startActivity(intent);
    }
}
