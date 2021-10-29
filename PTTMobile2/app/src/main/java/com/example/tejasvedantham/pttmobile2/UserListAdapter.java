package com.example.tejasvedantham.pttmobile2;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
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

import java.util.ArrayList;

import static com.example.tejasvedantham.pttmobile2.LoginActivity.userSession;

public class UserListAdapter extends ArrayAdapter<User> {

    private Context context;

    private static final String LOG_TAG = UserListAdapter.class.getSimpleName();
    private static final String CONFIRM_MSG = "The user has projects. Do you still want to delete it?";

    public UserListAdapter(Context context, ArrayList<User> data) {
        super(context, 0, data);
        this.context = context;

    }

    @Override
    public User getItem(int position) {
        return super.getItem(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        User user = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.admin_user_row_item, parent, false);
        }
        TextView firstNameText = (TextView) convertView.findViewById(R.id.firstNameText);
        TextView lastNameText = (TextView) convertView.findViewById(R.id.lastNameText);
        TextView emailText = (TextView) convertView.findViewById(R.id.emailText);

        firstNameText.setText(user.firstName);
        lastNameText.setText(user.lastName);
        emailText.setText(user.email);

        Button editProjectButton = (Button) convertView.findViewById(R.id.editUser);
        editProjectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                editUser(user);
            }
        });

        Button deleteUser = (Button) convertView.findViewById(R.id.deleteUser);
        deleteUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                deleteUser(user.id);
            }
        });

        return convertView;
    }
    public void deleteUser(String userId) {
        String url = BackendConnections.baseUrl + String.format("/users/%s/projects", userId);
        Log.d(LOG_TAG, "delete users check projects url: " + url);

        RequestQueue requestQueue = Volley.newRequestQueue(getContext());

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                if (response.length() > 0) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    DialogInterface.OnClickListener listener  = (dialog, confirm) -> {
                        switch (confirm){
                            case DialogInterface.BUTTON_POSITIVE:
                                deleteUserConfirm(userId);
                                break;
                        }
                    };
                    builder.setMessage("This user has projects as associated with it. Are you sure you want to delete?");
                    builder.setPositiveButton("Yes", listener);
                    builder.setNegativeButton("No", listener);
                    builder.show();
                } else {
                    deleteUserConfirm(userId);
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

    private void deleteUserConfirm(String userId) {
        String url = BackendConnections.baseUrl + String.format("/users/%s", userId);
        Log.d(LOG_TAG, "delete user url: " + url);

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

        Intent intent = new Intent(context.getApplicationContext(), AdminHomeActivity.class);
        context.startActivity(intent);
    }

    private void editUser(User user){
        Intent intent = new Intent(context.getApplicationContext(), EditUserActivity.class);

        intent.putExtra("firstName", user.firstName);
        intent.putExtra("lastName", user.lastName);
        intent.putExtra("email", user.email);
        intent.putExtra("userId", user.id);

        context.startActivity(intent);

    }

}
