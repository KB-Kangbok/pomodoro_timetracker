package com.example.tejasvedantham.pttmobile2;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.matcher.BundleMatchers.hasEntry;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static com.example.tejasvedantham.pttmobile2.CreateProjectActivityTest.createProjectActivity;
import static com.example.tejasvedantham.pttmobile2.CreateUserActivityTest.createUserActivity;
import static com.example.tejasvedantham.pttmobile2.UserListAdapter.CONFIRM_MSG;

import static org.hamcrest.CoreMatchers.not;

import android.content.Intent;

import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.matcher.BoundedMatcher;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import junit.framework.TestCase;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;

@RunWith(AndroidJUnit4.class)
public class AdminHomeActivityTest extends TestCase {

    @Rule
    public final static ActivityTestRule<AdminHomeActivity> adminHomeActivity = new ActivityTestRule<>(AdminHomeActivity.class, false, false);

    private static ArrayList<User> testUserList = new ArrayList<>();
    private Intent intent;

    public void setUp() throws Exception {
        super.setUp();
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
}
