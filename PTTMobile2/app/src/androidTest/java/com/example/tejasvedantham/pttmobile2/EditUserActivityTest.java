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

import static androidx.test.core.app.ApplicationProvider.getApplicationContext;
import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.hamcrest.Matchers.anything;

import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Random;

@RunWith(AndroidJUnit4.class)
public class EditUserActivityTest {

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
//    @Rule
//    public final ActivityTestRule<EditUserActivity> editUser = new ActivityTestRule<EditUserActivity>(EditUserActivity.class, false, false);
//    @Rule
//    public ActivityTestRule<AdminHomeActivity> admin =
//            new ActivityTestRule<>(AdminHomeActivity.class);
    public String id = "";
    public String firstname = "";
    public String lastname = "";
    public String email = "";
    @Test
    public void TestEditButton() {

        createDummyUser();
        try
        {
            Thread.sleep(1800);
        }
        catch(InterruptedException e)
        {

        }
        ActivityTestRule<AdminHomeActivity> adminIntentRule =
                new ActivityTestRule<>(AdminHomeActivity.class, false, false);
        Intents.init();
        Intent intent = new Intent();
        adminIntentRule.launchActivity(intent);

        onData(anything())
                .inAdapterView(withId(R.id.admin_user_list))
                .atPosition(0)
                .onChildView(withId(R.id.editUser))
                .check(matches(isDisplayed()))
                .perform(click());

        intended(hasComponent(EditUserActivity.class.getName()));
        deleteDummyUser();

    }



    @Test
    public void TestEditAllFields() {

        removeAllCurrent();
        createDummyUser();
        try
        {
            Thread.sleep(1800);
        }
        catch(InterruptedException e)
        {

        }
        Intents.init();

        ActivityTestRule<AdminHomeActivity> editIntent =
                new ActivityTestRule<>(AdminHomeActivity.class, false, false);

        Intent intent = new Intent();
//        intent.putExtra("firstName", firstname);
//        intent.putExtra("lastName", lastname);
//        intent.putExtra("email", email);
        editIntent.launchActivity(intent);


        onData(anything())
                .inAdapterView(withId(R.id.admin_user_list))
                .atPosition(0)
                .onChildView(withId(R.id.editUser))
                .check(matches(isDisplayed()))
                .perform(click());
//        Intents.release();

        onView(withId(R.id.userFirstNameEdit))
                .perform(typeText("2"), closeSoftKeyboard());
        onView(withId(R.id.userLastNameEdit))
                .perform(typeText("2"), closeSoftKeyboard());
        onView(withId(R.id.editUserButton)).perform(click());

        try
        {
            Thread.sleep(1800);
        }
        catch(InterruptedException e)
        {

        }


//        intended(hasComponent(AdminHomeActivity.class.getName()));
        onData(anything())
                .inAdapterView(withId(R.id.admin_user_list))
                .atPosition(0)
                .onChildView(withId(R.id.firstNameText))
                .check(matches(withText("Minyi2")));

        onData(anything())
                .inAdapterView(withId(R.id.admin_user_list))
                .atPosition(0)
                .onChildView(withId(R.id.lastNameText))
                .check(matches(withText("Lu2")));
        Intents.release();

    }

    @Test
    public void TestEditOnlyFirstName() {

        removeAllCurrent();
        createDummyUser();
        try
        {
            Thread.sleep(1800);
        }
        catch(InterruptedException e)
        {

        }
        Intents.init();

        ActivityTestRule<AdminHomeActivity> editIntent =
                new ActivityTestRule<>(AdminHomeActivity.class, false, false);

        Intent intent = new Intent();
//        intent.putExtra("firstName", firstname);
//        intent.putExtra("lastName", lastname);
//        intent.putExtra("email", email);
        editIntent.launchActivity(intent);


        onData(anything())
                .inAdapterView(withId(R.id.admin_user_list))
                .atPosition(0)
                .onChildView(withId(R.id.editUser))
                .check(matches(isDisplayed()))
                .perform(click());
//        Intents.release();

        onView(withId(R.id.userFirstNameEdit))
                .perform(typeText("2"), closeSoftKeyboard());

        onView(withId(R.id.editUserButton)).perform(click());

        try
        {
            Thread.sleep(1800);
        }
        catch(InterruptedException e)
        {

        }


//        intended(hasComponent(AdminHomeActivity.class.getName()));
        onData(anything())
                .inAdapterView(withId(R.id.admin_user_list))
                .atPosition(0)
                .onChildView(withId(R.id.firstNameText))
                .check(matches(withText("Minyi2")));

        onData(anything())
                .inAdapterView(withId(R.id.admin_user_list))
                .atPosition(0)
                .onChildView(withId(R.id.lastNameText))
                .check(matches(withText("Lu")));
        Intents.release();

    }

    @Test
    public void TestEditOnlyLastName() {

        removeAllCurrent();
        createDummyUser();
        try
        {
            Thread.sleep(1800);
        }
        catch(InterruptedException e)
        {

        }
        Intents.init();

        ActivityTestRule<AdminHomeActivity> editIntent =
                new ActivityTestRule<>(AdminHomeActivity.class, false, false);

        Intent intent = new Intent();
//        intent.putExtra("firstName", firstname);
//        intent.putExtra("lastName", lastname);
//        intent.putExtra("email", email);
        editIntent.launchActivity(intent);


        onData(anything())
                .inAdapterView(withId(R.id.admin_user_list))
                .atPosition(0)
                .onChildView(withId(R.id.editUser))
                .check(matches(isDisplayed()))
                .perform(click());
//        Intents.release();


        onView(withId(R.id.userLastNameEdit))
                .perform(typeText("2"), closeSoftKeyboard());
        onView(withId(R.id.editUserButton)).perform(click());

        try
        {
            Thread.sleep(1800);
        }
        catch(InterruptedException e)
        {

        }


//        intended(hasComponent(AdminHomeActivity.class.getName()));
        onData(anything())
                .inAdapterView(withId(R.id.admin_user_list))
                .atPosition(0)
                .onChildView(withId(R.id.firstNameText))
                .check(matches(withText("Minyi")));

        onData(anything())
                .inAdapterView(withId(R.id.admin_user_list))
                .atPosition(0)
                .onChildView(withId(R.id.lastNameText))
                .check(matches(withText("Lu2")));
        Intents.release();

    }
    @Test
    public void TestEditNoFields() {

        removeAllCurrent();
        createDummyUser();
        try
        {
            Thread.sleep(1800);
        }
        catch(InterruptedException e)
        {

        }
        Intents.init();

        ActivityTestRule<AdminHomeActivity> editIntent =
                new ActivityTestRule<>(AdminHomeActivity.class, false, false);

        Intent intent = new Intent();
//        intent.putExtra("firstName", firstname);
//        intent.putExtra("lastName", lastname);
//        intent.putExtra("email", email);
        editIntent.launchActivity(intent);


        onData(anything())
                .inAdapterView(withId(R.id.admin_user_list))
                .atPosition(0)
                .onChildView(withId(R.id.editUser))
                .check(matches(isDisplayed()))
                .perform(click());
//        Intents.release();


        onView(withId(R.id.editUserButton)).perform(click());

        try
        {
            Thread.sleep(1800);
        }
        catch(InterruptedException e)
        {

        }


//        intended(hasComponent(AdminHomeActivity.class.getName()));
        onData(anything())
                .inAdapterView(withId(R.id.admin_user_list))
                .atPosition(0)
                .onChildView(withId(R.id.firstNameText))
                .check(matches(withText("Minyi")));

        onData(anything())
                .inAdapterView(withId(R.id.admin_user_list))
                .atPosition(0)
                .onChildView(withId(R.id.lastNameText))
                .check(matches(withText("Lu")));
        Intents.release();

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
//                    username =  response.get("email").toString();
//                    Log.d("name", username);
                    id =  response.get("id").toString();
                    firstname = response.get("firstname").toString();
                    lastname =  response.get("lastname").toString();
                    email = response.get("email").toString();
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


