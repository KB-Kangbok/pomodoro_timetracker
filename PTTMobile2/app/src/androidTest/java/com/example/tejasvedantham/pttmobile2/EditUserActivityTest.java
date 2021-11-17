package com.example.tejasvedantham.pttmobile2;

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

import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class EditUserActivityTest {

    @Rule
    public ActivityTestRule<LoginActivity> loginIntentRule =
            new ActivityTestRule<>(LoginActivity.class);

    @Test
    public void TestEditButton() {

        onData(anything())
                .inAdapterView(withId(R.id.admin_user_list))
                .atPosition(0)
                .onChildView(withId(R.id.editUser))
                .check(matches(isDisplayed()))
                .perform(click());

        intended(hasComponent(EditUserActivity.class.getName()));
    }

    @Test
    public void TestEdit() {

        onView(withId(R.id.userFirstName))
                .perform(typeText("Minyi2"), closeSoftKeyboard());
        onView(withId(R.id.userLastName))
                .perform(typeText("Lu2"), closeSoftKeyboard());
        onView(withId(R.id.editUserButton)).perform(click());

        intended(hasComponent(AdminHomeActivity.class.getName()));
    }
}


