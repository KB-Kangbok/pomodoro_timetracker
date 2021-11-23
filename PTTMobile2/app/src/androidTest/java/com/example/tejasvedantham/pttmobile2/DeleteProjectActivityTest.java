package com.example.tejasvedantham.pttmobile2;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.IBinder;
import android.support.test.InstrumentationRegistry;
import android.util.Log;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.Toast;


import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import junit.framework.TestCase;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.Root;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.intent.Intents;
import androidx.test.rule.ActivityTestRule;

import static androidx.test.core.app.ApplicationProvider.getApplicationContext;
import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Rule;
import org.junit.Test;

import java.util.Random;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.instanceOf;

public class DeleteProjectActivityTest extends TestCase {

//    @Rule
//    public ActivityTestRule<DeleteProjectActivity> act = new ActivityTestRule<DeleteeProjectActivity>(DeleteProjectActivityActivity.class, false, false);

    private String projectName = "MyProject";


    public String id = "";
    public String firstname = "";
    public String lastname = "";
    public String username = "";

    public final static ActivityTestRule<UserHomeActivity> deleteProject = new ActivityTestRule<UserHomeActivity>(UserHomeActivity.class, false, false);
    @After
    public void tearDown() {
        Intents.release();
    }
    /** Test deleting a project with no associated time. */
    @Test
    public void testDeleteNoTime() {
        removeAllCurrent();
        try
        {
            Thread.sleep(1800);
        }
        catch(InterruptedException e)
        {

        }
        createDummyUser();
        try
        {
            Thread.sleep(1800);
        }
        catch(InterruptedException e)
        {

        }
        addProjectUser("MyProj");
        try
        {
            Thread.sleep(1800);
        }
        catch(InterruptedException e)
        {

        }
        addProjectUser("MyProj2");
        Intents.init();

        try
        {
            Thread.sleep(1800);
        }
        catch(InterruptedException e)
        {

        }

        Intent intent = new Intent();
        intent.putExtra("id", id);

        deleteProject.launchActivity(intent);

        onData(anything())
                .inAdapterView(withId(R.id.project_list))
                .atPosition(0)
                .onChildView(withId(R.id.deleteProject))
                .perform(click());
        try
        {
            Thread.sleep(1800);
        }
        catch(InterruptedException e)
        {

        }

        onData(anything())
                .inAdapterView(withId(R.id.project_list))
                .atPosition(0)
                .onChildView(withId(R.id.nameProject))
                .check(matches(withText("MyProj2")));
        deleteDummyUser();


    }
    String projectId = "";
    /** Test deleting a user with associated time and no provided on confirmation request. */
    @Test
    public void testDeleteWithTimeNoConfirm() {
        removeAllCurrent();
        try
        {
            Thread.sleep(1800);
        }
        catch(InterruptedException e)
        {

        }
        createDummyUser();
        try
        {
            Thread.sleep(1800);
        }
        catch(InterruptedException e)
        {

        }
        addProjectUser("MyProj");
        try
        {
            Thread.sleep(1800);
        }
        catch(InterruptedException e)
        {

        }
        addProjectUser("MyProj2");
        Intents.init();

        try
        {
            Thread.sleep(1800);
        }
        catch(InterruptedException e)
        {

        }
        addTimeToProj();

        Intent intent = new Intent();
        intent.putExtra("id", id);

        deleteProject.launchActivity(intent);
        try
        {
            Thread.sleep(1800);
        }
        catch(InterruptedException e)
        {

        }

        onData(anything())
                .inAdapterView(withId(R.id.project_list))
                .atPosition(0)
                .onChildView(withId(R.id.deleteProject))
                .perform(click());
        try
        {
            Thread.sleep(1800);
        }
        catch(InterruptedException e)
        {

        }
        onView(withText("The project has time already logged to it. Do you still want to delete it?")).check(matches(isDisplayed()));
        onView(withText("NO")).perform(click());
        onData(anything())
                .inAdapterView(withId(R.id.project_list))
                .atPosition(0)
                .onChildView(withId(R.id.nameProject))
                .check(matches(withText("MyProj")));
        onData(anything())
                .inAdapterView(withId(R.id.project_list))
                .atPosition(1)
                .onChildView(withId(R.id.nameProject))
                .check(matches(withText("MyProj2")));



    }
    /** Test deleting a user with associated time and yes provided on confirmation request. */
    @Test
    public void testDeleteWithTimeYesConfirm() {
        projectId = "";
        removeAllCurrent();
        try
        {
            Thread.sleep(1800);
        }
        catch(InterruptedException e)
        {

        }
        createDummyUser();
        try
        {
            Thread.sleep(1800);
        }
        catch(InterruptedException e)
        {

        }
        addProjectUser("MyProj");
        try
        {
            Thread.sleep(1800);
        }
        catch(InterruptedException e)
        {

        }
        addProjectUser("MyProj2");
        Intents.init();

        try
        {
            Thread.sleep(1800);
        }
        catch(InterruptedException e)
        {

        }
        addTimeToProj();

        Intent intent = new Intent();
        intent.putExtra("id", id);

        deleteProject.launchActivity(intent);

        onData(anything())
                .inAdapterView(withId(R.id.project_list))
                .atPosition(0)
                .onChildView(withId(R.id.deleteProject))
                .perform(click());
        try
        {
            Thread.sleep(1800);
        }
        catch(InterruptedException e)
        {

        }
        onView(withText("The project has time already logged to it. Do you still want to delete it?")).check(matches(isDisplayed()));
        onView(withText("Yes")).perform(click());
        onData(anything())
                .inAdapterView(withId(R.id.project_list))
                .atPosition(0)
                .onChildView(withId(R.id.nameProject))
                .check(matches(withText("MyProj2")));




    }

    private void createDummyUser() {

        JSONObject postData = new JSONObject();
        try {
            postData.put("firstName", "Minyi");
            postData.put("lastName", "Lu");

            postData.put("email", generateEmail());
        } catch (JSONException e) {
            e.printStackTrace();
        }
//        Log.d(LOG_TAG, "create user: " + postData.toString());

        String url = BackendConnections.baseUrl + "/users";
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, postData, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                Log.d("yes", "POST /users RES " + response);
//                myMethod(response.toString()).

                try {
                    username =  response.get("email").toString();
                    Log.d("name", username);
                    id =  response.get("id").toString();
                    Log.d("ID", id);
//                    System.out.println(id);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("errored");
                Log.d("not", "POST /users REQ FAILED");
            }
        });
        requestQueue.add(jsonObjectRequest);

    }

    private void deleteDummyUser() {

        String url = BackendConnections.baseUrl + String.format("/users/%s", id);
//        Log.d(LOG_TAG, "delete user url: " + url);

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.DELETE, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
//                Log.d(LOG_TAG, String.format("DELETE %s RES %s", url, response.toString()));
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
//                Log.d(LOG_TAG, String.format("DELETE %s FAILED", url));
            }
        });
        requestQueue.add(jsonObjectRequest);

    }
    private String generateEmail() {
        String email = "";
        Random r = new Random();
        for (int i = 0; i < 50; i++) {
            email = email + r.nextInt(10);
        }
        return email;
    }
    private void removeAllCurrent() {
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

    private void deleteUser(String id) {

        String url = BackendConnections.baseUrl + String.format("/users/%s", id);
//        Log.d(LOG_TAG, "delete user url: " + url);

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.DELETE, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("DELETE", String.format("DELETE %s RES %s", url, response.toString()));
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
//                Log.d(LOG_TAG, String.format("DELETE %s FAILED", url));
            }
        });
        requestQueue.add(jsonObjectRequest);

    }

    private void addProjectUser(String name){
        JSONObject postData = new JSONObject();
        try {
            postData.put("projectname", name);

        } catch (JSONException e) {
            e.printStackTrace();
        }
//        Log.d(LOG_TAG, "create user: " + postData.toString());

        String url = BackendConnections.baseUrl + "/users/" + id + "/projects";
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, postData, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                Log.d("yes", "POST /projects RES " + response);
//                myMethod(response.toString()).

                try {
                    if (projectId == "") {
                        projectId = response.get("id").toString();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("errored");
                Log.d("not", "POST /users REQ FAILED");
            }
        });
        requestQueue.add(jsonObjectRequest);
    }

    private void addTimeToProj(){
        String url = BackendConnections.baseUrl + String.format("/users/%s/projects/%s/sessions", id, projectId);
        JSONObject postData = new JSONObject();
        try {
            postData.put("startTime", "2019-02-18T20:00Z");
            postData.put("endTime", "2019-02-18T21:00Z");
            postData.put("counter", "0");


        } catch (JSONException e) {
            e.printStackTrace();
        }
//        Log.d(LOG_TAG, "create user: " + postData.toString());

//        String url = BackendConnections.baseUrl + "/users/" + id + "/projects";
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, postData, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                Log.d("yes", "POST /projects RES " + response);
//                myMethod(response.toString()).
//
//                try {
////                    projectId = response.get("id").toString();
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("errored");
                Log.d("not", "POST /users REQ FAILED");
            }
        });
        requestQueue.add(jsonObjectRequest);
    }

}