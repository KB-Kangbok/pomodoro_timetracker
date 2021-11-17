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

public class DeleteProjectActivityTest extends TestCase {

//    @Rule
//    public ActivityTestRule<CreateProjectActivity> act = new ActivityTestRule<CreateProjectActivity>(UserHomeActivity.class, false, false);
//
//    private String projectName = "MyProject";
//
//    public void setUp() throws Exception {
//        super.setUp();
//        Intents.init();
//    }
//
//    @Test
//    public void testDeleteProject() {
//        Intent intent = new Intent();
//        intent.putExtra("id", "1");
//
//        act.launchActivity(intent);
//
//        intended(hasComponent(UserHomeActivity.class.getName()));
//
//        onData(anything())
//                .inAdapterView(withId(R.id.project_list))
//                .atPosition(0)
//                .onChildView(withId(R.id.deleteProject))
//                .check(matches(isDisplayed()))
//                .perform(click());
//
//    }
//
//
//
//    public void tearDown() throws Exception {
//        Intents.release();
//    }
}