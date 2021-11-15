package com.example.tejasvedantham.pttmobile2;

import android.content.Intent;

import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import junit.framework.TestCase;

import org.junit.Rule;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class CreateUserActivityTest extends TestCase {

    @Rule
    public final static ActivityTestRule<CreateUserActivity> createUserActivity = new ActivityTestRule<>(CreateUserActivity.class, false, false);

    private Intent intent;

    public void setUp() throws Exception {
        super.setUp();
        Intents.init();
        intent = new Intent();
        createUserActivity.launchActivity(intent);
    }

    public void tearDown() throws Exception {
        Intents.release();
    }
}
