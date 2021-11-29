package com.example.tejasvedantham.pttmobile2;

import android.content.Intent;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Test;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import androidx.test.espresso.IdlingPolicies;
import androidx.test.espresso.intent.Intents;
import androidx.test.rule.ActivityTestRule;

import static androidx.test.core.app.ApplicationProvider.getApplicationContext;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

public class StartPomodoroStep2ActivityTest {

    private String projectName = "MyProject";

    public String id = "";
    public String firstname = "";
    public String lastname = "";
    public String username = "";
    public String projectId = "";

    @After
    public void tearDown() {
        Intents.release();
    }

    /** Test ending a pomodoro and starting a new one increments the number of pomodoros in this project and starts new pomodoro */
    @Test
    public void TestPomodoroTimerFinishYes() {
        ActivityTestRule<StartPomodoroStep2Activity> StartPomodoroStep2 =
                new ActivityTestRule<>(StartPomodoroStep2Activity.class, false, false);
        removeAllCurrent();
        createDummyUser();
        try
        {
            Thread.sleep(1800);
        }
        catch(InterruptedException e)
        {

        }

        List<Project> projects = new ArrayList<>();
        projects.add(new Project("MyProject", projectId));
        Intents.init();
        Intent intent = new Intent();
        intent.putExtra("id", id);
        intent.putExtra("projects", (Serializable)projects);
        intent.putExtra("projectId", "-1");
        intent.putExtra("projectName", "No Associated Project");
        StartPomodoroStep2.launchActivity(intent);

        StartPomodoroStep2.getActivity().countDownTimeInSeconds = 10;
        onView(withId(R.id.startPomodoroButton)).perform(click());
        try
        {
            Thread.sleep(10000);
        }
        catch(InterruptedException e)
        {

        }

        onView(withText(android.R.string.yes)).check(matches(isDisplayed()));
        onView(withText(android.R.string.yes)).perform(click());
        onView(withId(R.id.numPomodorosText)).check(matches(withText("Pomodoros in this session: 1")));

        deleteDummyUser();



    }
    /** Test ending a pomodoro and not starting a new one takes user back to home  */
    @Test
    public void TestPomodoroTimerFinishNo() {
        ActivityTestRule<StartPomodoroStep2Activity> StartPomodoroStep2 =
                new ActivityTestRule<>(StartPomodoroStep2Activity.class, false, false);
        removeAllCurrent();
        createDummyUser();
        try
        {
            Thread.sleep(1800);
        }
        catch(InterruptedException e)
        {

        }
        addProjectUser("MyProject");
        List<Project> projects = new ArrayList<>();
        projects.add(new Project("MyProject", projectId));
        Intents.init();
        Intent intent = new Intent();
        intent.putExtra("id", id);
        intent.putExtra("projects", (Serializable)projects);
        intent.putExtra("projectId", projectId);
        intent.putExtra("projectName", "MyProject");
        StartPomodoroStep2.launchActivity(intent);

        StartPomodoroStep2.getActivity().countDownTimeInSeconds = 10;
        onView(withId(R.id.startPomodoroButton)).perform(click());
        try
        {
            Thread.sleep(10000);
        }
        catch(InterruptedException e)
        {

        }

        onView(withText("No")).check(matches(isDisplayed()));
        onView(withText("No")).perform(click());
        intended(hasComponent(UserHomeActivity.class.getName()));

        deleteDummyUser();

    }
    /** Test stopping a pomodoro and a dialog box appears for confirmation about partial logging */
    @Test
    public void TestPomodoroTimerStopWithProject(){
        ActivityTestRule<StartPomodoroStep2Activity> StartPomodoroStep2 =
                new ActivityTestRule<>(StartPomodoroStep2Activity.class, false, false);

        removeAllCurrent();
        createDummyUser();
        try
        {
            Thread.sleep(1800);
        }
        catch(InterruptedException e)
        {

        }
        addProjectUser("MyProject");
        List<Project> projects = new ArrayList<>();
        projects.add(new Project("MyProject", projectId));
        Intents.init();
        Intent intent = new Intent();
        intent.putExtra("id", id);
        intent.putExtra("projects", (Serializable)projects);
        intent.putExtra("projectId", projectId);
        intent.putExtra("projectName", "MyProject");
        StartPomodoroStep2.launchActivity(intent);
        StartPomodoroStep2.getActivity().countDownTimeInSeconds = 18;

        onView(withId(R.id.startPomodoroButton)).perform(click());
        onView(withId(R.id.stopButton)).perform(click());

        onView(withText("Would you like to log this partial Pomodoro to this Project?")).check(matches(isDisplayed()));
        onView(withText("No")).check(matches(isDisplayed()));
        onView(withText("No")).perform(click());
        intended(hasComponent(UserHomeActivity.class.getName()));
        deleteDummyUser();


    }
    /** Test stopping a pomodoro not associated with project takes user back home and does not show confirmation dialog */
    @Test
    public void TestPomodoroTimerStopWithNoProject(){
        ActivityTestRule<StartPomodoroStep2Activity> StartPomodoroStep2 =
                new ActivityTestRule<>(StartPomodoroStep2Activity.class, false, false);

        removeAllCurrent();
        createDummyUser();
        try
        {
            Thread.sleep(1800);
        }
        catch(InterruptedException e)
        {

        }

        List<Project> projects = new ArrayList<>();
        projects.add(new Project("MyProject", projectId));
        Intents.init();
        Intent intent = new Intent();
        intent.putExtra("id", id);
        intent.putExtra("projects", (Serializable)projects);
        intent.putExtra("projectId", "-1");
        intent.putExtra("projectName", "No Associated Project");
        StartPomodoroStep2.launchActivity(intent);
        StartPomodoroStep2.getActivity().countDownTimeInSeconds = 18;

        onView(withId(R.id.startPomodoroButton)).perform(click());
        onView(withId(R.id.stopButton)).perform(click());


        intended(hasComponent(UserHomeActivity.class.getName()));
        deleteDummyUser();


    }
    /** Test stopping a pomodoro shows the confirmation dialog to log partial pomodoro and user selects yes  */
    @Test
    public void TestPomodoroTimerStopLogPartial() {


        ActivityTestRule<StartPomodoroStep2Activity> StartPomodoroStep2 =
                new ActivityTestRule<>(StartPomodoroStep2Activity.class, false, false);

        removeAllCurrent();
        createDummyUser();
        try
        {
            Thread.sleep(1800);
        }
        catch(InterruptedException e)
        {

        }
        addProjectUser("MyProject");
        List<Project> projects = new ArrayList<>();
        projects.add(new Project("MyProject", projectId));
        Intents.init();
        Intent intent = new Intent();
        intent.putExtra("id", id);
        intent.putExtra("projects", (Serializable)projects);
        intent.putExtra("projectId", projectId);
        intent.putExtra("projectName", "MyProject");
        StartPomodoroStep2.launchActivity(intent);
        StartPomodoroStep2.getActivity().countDownTimeInSeconds = 18;

        onView(withId(R.id.startPomodoroButton)).perform(click());
        onView(withId(R.id.stopButton)).perform(click());

        onView(withText("Would you like to log this partial Pomodoro to this Project?")).check(matches(isDisplayed()));
        onView(withText("Yes")).check(matches(isDisplayed()));
        onView(withText("Yes")).perform(click());
        intended(hasComponent(UserHomeActivity.class.getName()));
        deleteDummyUser();

    }
    /** Test stopping a pomodoro shows the confirmation dialog to log partial pomodoro and user selects no so partial pomodoro is not logged to system */
    @Test
    public void TestPomodoroTimerStopNoLogPartial() {
        // Yes to log partial
        ActivityTestRule<StartPomodoroStep2Activity> StartPomodoroStep2 =
                new ActivityTestRule<>(StartPomodoroStep2Activity.class, false, false);

        removeAllCurrent();
        createDummyUser();
        try
        {
            Thread.sleep(1800);
        }
        catch(InterruptedException e)
        {

        }
        addProjectUser("MyProject");
        List<Project> projects = new ArrayList<>();
        projects.add(new Project("MyProject", projectId));
        Intents.init();
        Intent intent = new Intent();
        intent.putExtra("id", id);
        intent.putExtra("projects", (Serializable)projects);
        intent.putExtra("projectId", projectId);
        intent.putExtra("projectName", "MyProject");
        StartPomodoroStep2.launchActivity(intent);
        StartPomodoroStep2.getActivity().countDownTimeInSeconds = 18;

        onView(withId(R.id.startPomodoroButton)).perform(click());
        onView(withId(R.id.stopButton)).perform(click());

        onView(withText("Would you like to log this partial Pomodoro to this Project?")).check(matches(isDisplayed()));
        onView(withText("No")).check(matches(isDisplayed()));
        onView(withText("No")).perform(click());
        intended(hasComponent(UserHomeActivity.class.getName()));
        deleteDummyUser();

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

}
