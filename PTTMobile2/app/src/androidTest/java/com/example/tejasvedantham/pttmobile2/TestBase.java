package com.example.tejasvedantham.pttmobile2;

import static androidx.test.core.app.ApplicationProvider.getApplicationContext;
import static com.example.tejasvedantham.pttmobile2.CreateProjectActivity.createProjectInternal;
import static com.example.tejasvedantham.pttmobile2.CreateUserActivity.createUserInternal;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import junit.framework.TestCase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

public class TestBase extends TestCase {

    private static final int TIME_OUT_SECONDS = 1;

    /** Remove all users in DB to make a clean start for testing. */
    public void removeAllCurrent() {
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        String url = BackendConnections.baseUrl + "/users";
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray jsonArray) {
                        Log.d("get", String.format("GET %s RES %s", url, jsonArray.toString()));
                        try {
                            JSONArray users = jsonArray;
                            boolean userFound = false;
                            for (int i = 0; i < users.length(); ++i) {
                                JSONObject user = (JSONObject) users.get(i);
                                String id = (user.get("id").toString());
                                deleteUser(id);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        requestQueue.add(jsonArrayRequest);
    }

    public void deleteUser(String id) {
        String url = BackendConnections.baseUrl + String.format("/users/%s", id);

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.DELETE, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("DELETE", String.format("DELETE %s RES %s", url, response.toString()));
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        requestQueue.add(jsonObjectRequest);
    }

    private String createdUserId = "0";
    public String createUser(User user) throws InterruptedException {
        createUserInternal(user, getApplicationContext(), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    createdUserId = response.getString("id");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        TimeUnit.SECONDS.sleep(TIME_OUT_SECONDS); // wait for the response before return it
        return createdUserId;
    }

    private String createdProjectId;
    public synchronized String createProject(String userId, Project project) throws InterruptedException {
        createProjectInternal(project, userId, getApplicationContext(), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    createdProjectId = response.getString("id");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        TimeUnit.SECONDS.sleep(TIME_OUT_SECONDS); // wait for the response before return it
        return createdProjectId;
    }

    private String createdSessionId;
    public synchronized String createSession(String userId, String projectId, Session session) throws InterruptedException {
        createSessionInternal(userId, projectId, session, getApplicationContext(), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    createdSessionId = response.getString("id");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        TimeUnit.SECONDS.sleep(TIME_OUT_SECONDS); // wait for the response before return it
        return createdSessionId;
    }

    private void createSessionInternal(String userId, String projectId, Session session, Context context, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
        String url = BackendConnections.baseUrl + String.format("/users/%s/projects/%s/sessions", userId, projectId);
        JSONObject postData = new JSONObject();
        try {
            postData.put("startTime", session.startingTime);
            postData.put("endTime", session.endingTime);
            postData.put("counter", Integer.toString(session.counter));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, postData, listener, errorListener);
        requestQueue.add(jsonObjectRequest);
    }

}
