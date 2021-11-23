package com.example.tejasvedantham.pttmobile2;

import static androidx.test.core.app.ApplicationProvider.getApplicationContext;
import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.assertThat;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withSpinnerText;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static com.example.tejasvedantham.pttmobile2.CreateProjectActivity.createProjectInternal;
import static com.example.tejasvedantham.pttmobile2.CreateUserActivity.createUserInternal;
import static com.example.tejasvedantham.pttmobile2.UserListAdapter.CONFIRM_MSG;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.anything;

import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.view.WindowManager;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import androidx.test.espresso.Root;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.matcher.BoundedMatcher;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import junit.framework.TestCase;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@RunWith(AndroidJUnit4.class)

public class StartPomodoroStep1ActivityTest extends TestCase {

    private String projectName = "MyProject";

    public String id = "";
    public String firstname = "";
    public String lastname = "";
    public String username = "";
    public String projectId = "";

    /** Test whether the projects associated to a user shows up in a pomodoro start attempt to associate to a project. */
    @Test
    public void TestProjectShowsInProjectListSpinnerPomodoro() {
        ActivityTestRule<StartPomodoroStep1Activity> StartPomodoroStep1 =
                new ActivityTestRule<>(StartPomodoroStep1Activity.class, false, false);
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
        StartPomodoroStep1.launchActivity(intent);


        onView(withId(R.id.project_spinner)).perform(click());
        onData(anything()).atPosition(1).perform(click());
        onView(withId(R.id.project_spinner)).check(matches(withSpinnerText("MyProject")));
        deleteDummyUser();
        Intents.release();
    }
    /** Test whether there is a option for starting pomodoro with no project. */
    @Test
    public void TestNoProjectShowsInProjectListSpinnerPomodoro() {
        ActivityTestRule<StartPomodoroStep1Activity> StartPomodoroStep1 =
                new ActivityTestRule<>(StartPomodoroStep1Activity.class, false, false);
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
        StartPomodoroStep1.launchActivity(intent);


        onView(withId(R.id.project_spinner)).perform(click());
        onData(anything()).atPosition(0).perform(click());
        onView(withId(R.id.project_spinner)).check(matches(withSpinnerText("No Associated Project")));
        deleteDummyUser();
        Intents.release();
    }
    /** Test starting pomodoro with no associated project. */
    @Test
    public void TestStartPomodoroButtonWithNoProject() {
        ActivityTestRule<StartPomodoroStep1Activity> StartPomodoroStep1 =
                new ActivityTestRule<>(StartPomodoroStep1Activity.class, false, false);

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

        StartPomodoroStep1.launchActivity(intent);

        onView(withId(R.id.project_spinner)).perform(click());
        onData(anything()).atPosition(0).perform(click());
        onView(withId(R.id.start_pomodoro_to_step_2_button)).perform(click());

        // Check activity change
        intended(hasComponent(StartPomodoroStep2Activity.class.getName()));
        onView(withId(R.id.projectNameText)).check(matches(withText("No Associated Project")));
        deleteDummyUser();
        Intents.release();
    }
    /** Test starting pomodoro with  associated project. */
    @Test
    public void TestStartPomodoroButtonWithProject() {
        ActivityTestRule<StartPomodoroStep1Activity> StartPomodoroStep1 =
                new ActivityTestRule<>(StartPomodoroStep1Activity.class, false, false);

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

        StartPomodoroStep1.launchActivity(intent);

        onView(withId(R.id.project_spinner)).perform(click());
        onData(anything()).atPosition(1).perform(click());
        onView(withId(R.id.start_pomodoro_to_step_2_button)).perform(click());

        // Check activity change
        intended(hasComponent(StartPomodoroStep2Activity.class.getName()));
        onView(withId(R.id.projectNameText)).check(matches(withText("MyProject")));
        deleteDummyUser();
        Intents.release();
    }
    /** Test starting pomodoro with associated project and whether number of pomodoros in this project is initialized. */
    @Test
    public void TestStartPomodoroButtonWithProjectIntializeNumberProjects() {
        ActivityTestRule<StartPomodoroStep1Activity> StartPomodoroStep1 =
                new ActivityTestRule<>(StartPomodoroStep1Activity.class, false, false);

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

        StartPomodoroStep1.launchActivity(intent);

        onView(withId(R.id.project_spinner)).perform(click());
        onData(anything()).atPosition(1).perform(click());
        onView(withId(R.id.start_pomodoro_to_step_2_button)).perform(click());

        // Check activity change
        intended(hasComponent(StartPomodoroStep2Activity.class.getName()));
        onView(withId(R.id.projectNameText)).check(matches(withText("MyProject")));
        onView(withId(R.id.numPomodorosText)).check(matches(withText("Pomodoros in this session: 0")));
        deleteDummyUser();
        Intents.release();
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
