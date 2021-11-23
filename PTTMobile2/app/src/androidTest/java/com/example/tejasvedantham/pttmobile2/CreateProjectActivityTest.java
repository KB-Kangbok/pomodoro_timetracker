package com.example.tejasvedantham.pttmobile2;

import android.content.Context;
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

public class CreateProjectActivityTest extends TestCase {


    @Rule
    public final static ActivityTestRule<CreateProjectActivity> createProjectActivity = new ActivityTestRule<CreateProjectActivity>(CreateProjectActivity.class, false, false);

    private String projectName = "MyProject";

    public void setUp() throws Exception {
        super.setUp();
        Intents.init();
    }
    /** Test creating a project that should succeed since new project name is provided. */
    public String id = "";
    @Test
    public void testProjectNameSuccess() {
        removeAllCurrent();
        createDummyUser();

        try
        {
            Thread.sleep(1800);
        }
        catch(InterruptedException e)
        {

        }

        Intent intent = new Intent();
        intent.putExtra("id", id);

        createProjectActivity.launchActivity(intent);

        Espresso.onView(withId(R.id.projectName))
                .perform(typeText(projectName));
        Espresso.closeSoftKeyboard();

        Espresso.onView(withId(R.id.createProjectButton)).perform(click());

        intended(hasComponent(UserHomeActivity.class.getName()));
        onData(anything())
            .inAdapterView(withId(R.id.project_list))
                .atPosition(0)
                .onChildView(withId(R.id.nameProject))
                .check(matches(withText("MyProject")));


    }
    /** Test creating a project with a used project name. */
    @Test
    public void testProjectNameTaken() {
        removeAllCurrent();
        createDummyUser();
        try
        {
            Thread.sleep(1800);
        }
        catch(InterruptedException e) {

        }
        createDummyProject(id);

        try
        {
            Thread.sleep(1800);
        }
        catch(InterruptedException e) {

        }

        Intent intent = new Intent();
        intent.putExtra("id", id);

        createProjectActivity.launchActivity(intent);


        Espresso.onView(withId(R.id.projectName))
                .perform(typeText(projectName));
        Espresso.closeSoftKeyboard();
        Espresso.onView(withId(R.id.createProjectButton)).perform(click());
        onView(withText("Project Name Already Taken")).inRoot(new ToastMatcher()).check(matches(isDisplayed()));

    }
    /** Test creating a project with no provided project name. */
    @Test
    public void testProjectNameEmpty() {
        removeAllCurrent();
        createDummyUser();
        try
        {
            Thread.sleep(1800);
        }
        catch(InterruptedException e) {

        }
        createDummyProject(id);

        try
        {
            Thread.sleep(1800);
        }
        catch(InterruptedException e) {

        }

        Intent intent = new Intent();
        intent.putExtra("id", id);

        createProjectActivity.launchActivity(intent);



        Espresso.closeSoftKeyboard();
        Espresso.onView(withId(R.id.createProjectButton)).perform(click());
        onView(withText("Please provide a name for the project")).inRoot(new ToastMatcher()).check(matches(isDisplayed()));

    }



    public void tearDown() throws Exception {
        Intents.release();
    }

    private void createDummyProject(String id) {
        JSONObject postData = new JSONObject();
        try {
            postData.put("projectname", projectName);
        } catch (JSONException e) {
            e.printStackTrace();
        }

//        Log.d(LOG_TAG, "create project: " + postData.toString());
        String url = BackendConnections.baseUrl + String.format("/users/%s/projects", id);
//        Log.d(LOG_TAG, "to url: " + url);

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, postData, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d("POST", String.format("POST %s RES %s", url, response));

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Error", String.format("POST %s REQ FAILED", url));
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
//   Retrieved from http://www.qaautomated.com/2016/01/how-to-test-toast-message-using-espresso.html
    public class ToastMatcher extends TypeSafeMatcher<Root> {

        @Override    public void describeTo(Description description) {
            description.appendText("is toast");
        }

        @Override    public boolean matchesSafely(Root root) {
            int type = root.getWindowLayoutParams().get().type;
            if ((type == WindowManager.LayoutParams.TYPE_TOAST)) {
                IBinder windowToken = root.getDecorView().getWindowToken();
                IBinder appToken = root.getDecorView().getApplicationWindowToken();
                if (windowToken == appToken) {
                    return true;
                }
            }
            return false;
        }

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

    }