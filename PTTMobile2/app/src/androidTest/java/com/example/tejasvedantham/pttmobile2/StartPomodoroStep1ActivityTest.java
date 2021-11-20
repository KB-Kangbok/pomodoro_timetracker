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

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@RunWith(AndroidJUnit4.class)

public class StartPomodoroStep1ActivityTest extends TestCase {

    @Test
    public void TestCreateProjectShowsInProjectListSpinner() {
        ActivityTestRule<StartPomodoroStep1Activity> StartPomodoroStep1 =
                new ActivityTestRule<>(StartPomodoroStep1Activity.class, false, false);

        Intents.init();
        Intent intent = new Intent();
        StartPomodoroStep1.launchActivity(intent);

        CreateProjectActivityTest createProjectHome = new CreateProjectActivityTest();
        createProjectHome.testProjectNameSuccess();

        onView(withId(R.id.project_spinner)).perform(click());
        onData(anything()).atPosition(1).perform(click());
        onView(withId(R.id.project_spinner)).check(matches(withSpinnerText("My Project")));
    }

    @Test
    public void TestStartPomodoroButtonWithNoProject() {
        ActivityTestRule<StartPomodoroStep1Activity> StartPomodoroStep1 =
                new ActivityTestRule<>(StartPomodoroStep1Activity.class, false, false);

        Intents.init();
        Intent intent = new Intent();
        StartPomodoroStep1.launchActivity(intent);

        onView(withId(R.id.start_pomodoro_to_step_2_button)).perform(click());

        // Check activity change
        intended(hasComponent(StartPomodoroStep2Activity.class.getName()));
        onView(withId(R.id.projectNameText)).check(matches(withSpinnerText("No Associated Project")));
    }

    @Test
    public void TestStartPomodoroButtonWithProject() {
        ActivityTestRule<StartPomodoroStep1Activity> StartPomodoroStep1 =
                new ActivityTestRule<>(StartPomodoroStep1Activity.class, false, false);

        Intents.init();
        Intent intent = new Intent();
        StartPomodoroStep1.launchActivity(intent);

        CreateProjectActivityTest createProjectHome = new CreateProjectActivityTest();
        createProjectHome.testProjectNameSuccess();

        TestCreateProjectShowsInProjectListSpinner();

        onView(withId(R.id.start_pomodoro_to_step_2_button)).perform(click());

        // Check activity change
        intended(hasComponent(StartPomodoroStep2Activity.class.getName()));
        onView(withId(R.id.projectNameText)).check(matches(withSpinnerText("My Project")));
    }

    @Test
    public void TestPomodoroTimerFinish(){
        ActivityTestRule<StartPomodoroStep2Activity> StartPomodoroStep2 =
                new ActivityTestRule<>(StartPomodoroStep2Activity.class, false, false);

        Intents.init();
        Intent intent = new Intent();

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
        onView(withId(R.id.timerText)).check(matches(withText("Time's up!");
        onView(withText(android.R.string.yes)).check(matches(isDisplayed()));
    }

    @Test
    public void TestPomodoroTimerFinishYes() {
        TestPomodoroTimerFinish();
        onView(withText(android.R.string.yes)).perform(click());

        try
        {
            Thread.sleep(10000);
        }
        catch(InterruptedException e)
        {

        }
        onView(withId(R.id.timerText)).check(matches(withText("Time's up!");
        onView(withText(android.R.string.yes)).check(matches(isDisplayed()));
        onView(withText(android.R.string.no)).check(matches(isDisplayed()));

    }

    @Test
    public void TestPomodoroTimerFinishNo() {
        TestPomodoroTimerFinish();
        onView(withText(android.R.string.no)).perform(click());

        // TODO: check log session

        intended(hasComponent(UserHomeActivity.class.getName()));
    }

    @Test
    public void TestPomodoroTimerStop(){
        ActivityTestRule<StartPomodoroStep2Activity> StartPomodoroStep2 =
                new ActivityTestRule<>(StartPomodoroStep2Activity.class, false, false);

        Intents.init();
        Intent intent = new Intent();

        StartPomodoroStep2.launchActivity(intent);
        StartPomodoroStep2.getActivity().countDownTimeInSeconds = 18;

        onView(withId(R.id.startPomodoroButton)).perform(click());
        try
        {
            Thread.sleep(2000);
        }
        catch(InterruptedException e)
        {

        }
        onView(withId(R.id.stopButton)).perform(click());
        onView(withText(android.R.string.yes)).check(matches(isDisplayed()));
        onView(withText(android.R.string.no)).check(matches(isDisplayed()));

    }

    @Test
    public void TestPomodoroTimerStopYes() {
        // Yes to log partial
        TestPomodoroTimerFinish();
        onView(withText(android.R.string.yes)).perform(click());

        // TODO: check log session

        intended(hasComponent(UserHomeActivity.class.getName()));
    }
    public void TestPomodoroTimerStopNo() {
        // Yes to log partial
        TestPomodoroTimerFinish();
        onView(withText(android.R.string.no)).perform(click());

        // TODO: check log session

        intended(hasComponent(UserHomeActivity.class.getName()));
    }



}
