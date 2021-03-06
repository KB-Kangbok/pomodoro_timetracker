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
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
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

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@RunWith(AndroidJUnit4.class)
public class AdminHomeActivityTest extends TestCase {

    private static final String LOG_TAG = AdminHomeActivityTest.class.getSimpleName();

    @Rule
    public final ActivityTestRule<AdminHomeActivity> adminHomeRule =
            new ActivityTestRule<>(AdminHomeActivity.class, false, false);

    @Rule
    public final ActivityTestRule<CreateUserActivity> createUserRule =
            new ActivityTestRule<>(CreateUserActivity.class, false, false);

    private ArrayList<User> testUserList = new ArrayList<>();
    public ArrayList<User> createdUserList = new ArrayList<>();
    private Intent intent;

    /** Remove all users in DB to make a clean start for testing. */
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
    /** Helper method to remove a specific user. */
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

    @Before
    public void setUp() throws Exception {
        super.setUp();
        Intents.init();
        intent = new Intent();

        removeAllCurrent();
        TimeUnit.SECONDS.sleep(3);

        testUserList.add(new User("Saira", "Poonnen", "saira.poonnen@gatech.edu", null));
        testUserList.add(new User("Minyi", "Lu", "minyi.lu@gatech.edu", null));
        testUserList.add(new User("Tejas", "Vedantham", "tejas.vedantham@gatech.edu", null));
        testUserList.add(new User("Yuyi", "Qu", "yuyi.qu@gatech.edu", null));
    }

    @After
    public void tearDown() throws Exception {
        Intents.release();
    }

    /** Test whether create button is visible. */
    @Test
    public void TestCreateButton() {
        // Type text and then press the button.
        createUserRule.launchActivity(intent);
        onView(withId(R.id.createUserButton)).perform(click());

        intended(hasComponent(CreateUserActivity.class.getName()));
    }
    /** Test creating a user with a first name, last name and email. */
    public String email = "";
    @Test
    public void TestCreatingUserAllFields() {
        createUserRule.launchActivity(intent);
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
    /** Test creating a user with a last name and email but no first name */
    @Test
    public void TestCreatingUserNoFirstName() {
        createUserRule.launchActivity(intent);
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
    /** Test creating a user with a first name and email but no last name. */
    @Test
    public void TestCreatingUserNoLastName() {
        createUserRule.launchActivity(intent);
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
    /** Test creating a user with no email */
    @Test
    public void TestCreatingUserNoEmail() {
        createUserRule.launchActivity(intent);
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
    /** Test creating a user with an email that is already used . */
    public String username = "";
    public String id = "";
    @Test
    public void TestCreatingUserEmailTaken() {
        createUserRule.launchActivity(intent);

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

    /** Test deleting a user with no associated projects. */
    @Test
    public void testDeleteUserWithoutProjects() throws InterruptedException {
        setupUsers();
        TimeUnit.SECONDS.sleep(3);
        assert(createdUserList.size() > 0);

        adminHomeRule.launchActivity(intent);
        onData(withContent(testUserList.get(0))).inAdapterView(withId(R.id.admin_user_list)).onChildView(withId(R.id.deleteUser)).perform(click());
        onView(withId(R.id.admin_user_list)).check(matches(not(withContent(testUserList.get(0)))));
    }
    /** Test deleting a user with associated projects. */
    @Test
    public void testDeleteUserWithProjects() throws InterruptedException {
        setupUsers();
        TimeUnit.SECONDS.sleep(3);
        assert(createdUserList.size() > 0);

        int userIndexToTest = 0;
        setupProjects(createdUserList.get(userIndexToTest).id);

        adminHomeRule.launchActivity(intent);
        onData(withContent(createdUserList.get(userIndexToTest))).inAdapterView(withId(R.id.admin_user_list)).onChildView(withId(R.id.deleteUser)).perform(click());
        onView(withText(CONFIRM_MSG)).check(matches(isDisplayed()));
    }

    private void setupUsers() {
        for (User user : testUserList) {
            createUserInternal(user, getApplicationContext(), new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        createdUserList.add(new User(response.get("firstName").toString(), response.get("lastName").toString(), response.get("email").toString(), response.get("id").toString()));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });
        }
    }

    private void setupProjects(String userId) {
        createProjectInternal(new Project("myProject1", null), userId, getApplicationContext(), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
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
