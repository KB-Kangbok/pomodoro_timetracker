package com.example.tejasvedantham.pttmobile2;

import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.test.InstrumentationRegistry;
import android.view.WindowManager;
import android.widget.ListView;


import junit.framework.TestCase;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.Root;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.intent.Intents;
import androidx.test.rule.ActivityTestRule;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;

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

    @Test
    public void testProjectNameSuccess() {
        Intent intent = new Intent();
        intent.putExtra("id", "1");

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

    @Test
    public void testProjectNameTaken() {
        Intent intent = new Intent();
        intent.putExtra("id", "1");

        createProjectActivity.launchActivity(intent);

        Espresso.onView(withId(R.id.projectName))
                .perform(typeText(projectName));
        Espresso.closeSoftKeyboard();

        Espresso.onView(withId(R.id.createProjectButton)).perform(click());

        onView(withText("Project Name Already Taken")).inRoot(new ToastMatcher()).check(matches(isDisplayed()));



    }



    public void tearDown() throws Exception {
        Intents.release();
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