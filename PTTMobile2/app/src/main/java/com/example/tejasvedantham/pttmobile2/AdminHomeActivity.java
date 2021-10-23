package com.example.tejasvedantham.pttmobile2;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class AdminHomeActivity extends AppCompatActivity {
    private static final String LOG_TAG = AdminHomeActivity.class.getSimpleName();
    private BackendConnections backendConnections;

    private AdminListAdapter adminListAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);

        backendConnections = new BackendConnections(this);
        backendConnections.addHeader("Authorization", "EMPTY FOR NOW");

        backendConnections.ExecuteHTTPRequest("/users", Request.Method.GET, null, new BackendConnections.VolleyCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                Log.d(LOG_TAG,"GET /users RES " + response);

                try {
                    JSONArray jsonArray = new JSONArray(response);
                    for(int i = 0; i < jsonArray.length(); i++){
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String email = jsonObject.getString("email");
                        String firstName = jsonObject.getString("firstName");
                        String lastName = jsonObject.getString("lastName");

                    }
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

    public void DeleteUser(){

    }

}
