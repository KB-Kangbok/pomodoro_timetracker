package com.example.tejasvedantham.pttmobile2;

import static androidx.test.core.app.ApplicationProvider.getApplicationContext;
import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.BundleMatchers.hasEntry;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static androidx.test.internal.util.Checks.checkNotNull;
import static com.example.tejasvedantham.pttmobile2.CreateProjectActivityTest.createProjectActivity;
import static com.example.tejasvedantham.pttmobile2.CreateUserActivityTest.createUserActivity;
import static com.example.tejasvedantham.pttmobile2.UserListAdapter.CONFIRM_MSG;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.EasyMock2Matchers.equalTo;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.core.AllOf.allOf;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.hamcrest.core.StringStartsWith.startsWith;
import static org.hamcrest.object.HasToString.hasToString;

import android.content.Intent;
import android.os.IBinder;
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

import androidx.test.espresso.Root;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.matcher.BoundedMatcher;
import androidx.test.espresso.matcher.ViewMatchers;
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

import java.util.ArrayList;
import java.util.Map;
import java.util.Random;

@RunWith(AndroidJUnit4.class)
public class AdminHomeActivityTest extends TestCase {

    @Rule
    public  ActivityTestRule<AdminHomeActivity> adminHomeActivity = new ActivityTestRule<>(AdminHomeActivity.class, false, false);

    private static ArrayList<User> testUserList = new ArrayList<>();
    private Intent intent;

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

    public void setUp() throws Exception {
        super.setUp();
        removeAllCurrent();
        testUserList.add(new User("Saira", "Poonnen", "saira.poonnen@gatech.edu", null));
        testUserList.add(new User("Minyi", "Lu", "minyi.lu@gatech.edu", null));
        testUserList.add(new User("Tejas", "Vedantham", "tejas.vedantham@gatech.edu", null));
        testUserList.add(new User("Yuyi", "Qu", "yuyi.qu@gatech.edu", null));

        Intents.init();
        intent = new Intent();

        setupUsers();
    }

    public void tearDown() throws Exception {
        Intents.release();
    }
    @Test
    public void TestCreateButton() {
        // Type text and then press the button.
        ActivityTestRule<AdminHomeActivity> adminIntentRule =
                new ActivityTestRule<>(AdminHomeActivity.class, false, false);
        Intents.init();
        Intent intent = new Intent();
        adminIntentRule.launchActivity(intent);
        onView(withId(R.id.createUserButton)).perform(click());

        intended(hasComponent(CreateUserActivity.class.getName()));
        Intents.release();
    }
    public String email = "";
    @Test
    public void TestCreatingUserAllFields() {
        ActivityTestRule<CreateUserActivity> adminIntentRule =
                new ActivityTestRule<>(CreateUserActivity.class, false, false);
        Intents.init();
        Intent intent = new Intent();
        adminIntentRule.launchActivity(intent);
        removeAllCurrent();
        // Type text and then press the button.
        onView(withId(R.id.userFirstName))
                .perform(typeText("Min"), closeSoftKeyboard());
        onView(withId(R.id.userLastName))
                .perform(typeText("Lu"), closeSoftKeyboard());
        email = generateEmail();
        onView(withId(R.id.userEmail))
                .perform(typeText(email), closeSoftKeyboard());
        onView(withId(R.id.createUserButton)).perform(click());
        Log.d("in","Here");

        intended(hasComponent(AdminHomeActivity.class.getName()));
        onData(anything())
                .inAdapterView(withId(R.id.admin_user_list))
                .atPosition(0)
                .onChildView(withId(R.id.emailText))
                .check(matches(withText(email)));

    }




    @Test
    public void TestCreatingUserNoFirstName() {
        ActivityTestRule<CreateUserActivity> adminIntentRule =
                new ActivityTestRule<>(CreateUserActivity.class, false, false);
        Intents.init();
        Intent intent = new Intent();
        adminIntentRule.launchActivity(intent);
        removeAllCurrent();
        // Type text and then press the button.
        onView(withId(R.id.userFirstName))
                .perform(typeText(""), closeSoftKeyboard());
        onView(withId(R.id.userLastName))
                .perform(typeText("Lu"), closeSoftKeyboard());
        email = generateEmail();
        onView(withId(R.id.userEmail))
                .perform(typeText(email), closeSoftKeyboard());
        onView(withId(R.id.createUserButton)).perform(click());
        Log.d("in","Here");

        intended(hasComponent(AdminHomeActivity.class.getName()));
        onData(anything())
                .inAdapterView(withId(R.id.admin_user_list))
                .atPosition(0)
                .onChildView(withId(R.id.emailText))
                .check(matches(withText(email)));
    }

    @Test
    public void TestCreatingUserNoLastName() {
        ActivityTestRule<CreateUserActivity> adminIntentRule =
                new ActivityTestRule<>(CreateUserActivity.class, false, false);
        Intents.init();
        Intent intent = new Intent();
        adminIntentRule.launchActivity(intent);
        removeAllCurrent();
        // Type text and then press the button.
        onView(withId(R.id.userFirstName))
                .perform(typeText("Min"), closeSoftKeyboard());
        onView(withId(R.id.userLastName))
                .perform(typeText(""), closeSoftKeyboard());
        email = generateEmail();
        onView(withId(R.id.userEmail))
                .perform(typeText(email), closeSoftKeyboard());
        onView(withId(R.id.createUserButton)).perform(click());
        Log.d("in","Here");

        intended(hasComponent(AdminHomeActivity.class.getName()));
        onData(anything())
                .inAdapterView(withId(R.id.admin_user_list))
                .atPosition(0)
                .onChildView(withId(R.id.emailText))
                .check(matches(withText(email)));
    }


    @Test
    public void TestCreatingUserNoEmail() {
        ActivityTestRule<CreateUserActivity> adminIntentRule =
                new ActivityTestRule<>(CreateUserActivity.class, false, false);
        Intents.init();
        Intent intent = new Intent();
        adminIntentRule.launchActivity(intent);
        // Type text and then press the button.
        onView(withId(R.id.userFirstName))
                .perform(typeText("Min"), closeSoftKeyboard());
        onView(withId(R.id.userLastName))
                .perform(typeText("Lu"), closeSoftKeyboard());
        onView(withId(R.id.userEmail))
                .perform(typeText(""), closeSoftKeyboard());
        onView(withId(R.id.createUserButton)).perform(click());

        onView(withText("Please provide an email address")).inRoot(new AdminHomeActivityTest.ToastMatcher()).check(matches(isDisplayed()));
    }

    public String username = "";
    public String id = "";
    @Test
    public void TestCreatingUserEmailTaken() {
        ActivityTestRule<CreateUserActivity> adminIntentRule =
                new ActivityTestRule<>(CreateUserActivity.class, false, false);
        Intents.init();
        Intent intent = new Intent();

        adminIntentRule.launchActivity(intent);

        createDummyUser();
        try
        {
            Thread.sleep(1800);
        }
        catch(InterruptedException e)
        {

        }
        // Type text and then press the button.
        onView(withId(R.id.userFirstName))
                .perform(typeText("Minyi"), closeSoftKeyboard());
        onView(withId(R.id.userLastName))
                .perform(typeText("Lu"), closeSoftKeyboard());
        onView(withId(R.id.userEmail))
                .perform(typeText(username), closeSoftKeyboard());
        onView(withId(R.id.createUserButton)).perform(click());

        onView(withText("Email Already Taken")).inRoot(new AdminHomeActivityTest.ToastMatcher()).check(matches(isDisplayed()));
        deleteDummyUser();
    }

    @Test
    public void testDeleteUserWithoutProjects() {
        adminHomeActivity.launchActivity(intent);
        onData(withContent(testUserList.get(0))).inAdapterView(withId(R.id.admin_user_list)).onChildView(withId(R.id.deleteUser)).perform(click());
        onView(withId(R.id.admin_user_list)).check(matches(not(withContent(testUserList.get(0)))));
    }

    @Test
    public void testDeleteUserWithProjects() {
        setupProjects();
        adminHomeActivity.launchActivity(intent);
        onData(withContent(testUserList.get(4))).inAdapterView(withId(R.id.admin_user_list)).onChildView(withId(R.id.deleteUser)).perform(click());
        onView(withText(CONFIRM_MSG)).check(matches(isDisplayed()));
    }

    private void setupUsers() {
        for (User user : testUserList) {
            createUserActivity.launchActivity(intent);
            onView(withId(R.id.userFirstName)).perform(typeText(user.firstName));
            onView(withId(R.id.userLastName)).perform(typeText(user.lastName));
            onView(withId(R.id.userEmail)).perform(typeText(user.email));
            onView(withId(R.id.createUserButton)).perform(click());
        }
    }

    private void setupProjects() {
        Intent userIntent = new Intent();
        userIntent.putExtra("id", "4"); // FIXME: how to know the id of the user I created before?
        createProjectActivity.launchActivity(userIntent);
        onView(withId(R.id.projectName)).perform(typeText("myProject1"));
        onView(withId(R.id.createProjectButton)).perform(click());
    }

    private static Matcher<Object> withContent(final User content) {
        return new BoundedMatcher<Object, User>(User.class) {
            @Override
            public boolean matchesSafely(User myObj) {
                return myObj.firstName.equals(content.firstName) && myObj.lastName.equals(content.lastName) && myObj.email.equals(content.email);
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("with content '" + content + "'");
            }
        };
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
}
