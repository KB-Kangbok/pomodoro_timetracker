package com.example.tejasvedantham.pttmobile2;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class UserStory1Test {

    @Rule
    public ActivityTestRule<LoginActivity> loginIntentRule =
            new ActivityTestRule<>(LoginActivity.class);

    @Test
    public void TestAdminLogin() {
        // Type text and then press the button.
        onView(withId(R.id.usernameField))
                .perform(typeText("admin"), closeSoftKeyboard());
        onView(withId(R.id.loginButton)).perform(click());

        // Check activity change
        intended(hasComponent(AdminHomeActivity.class.getName()));
    }
}

@RunWith(AndroidJUnit4.class)
class UserStory1TestCreateUserButton {

    public ActivityTestRule<AdminHomeActivity> adminIntentRule =
            new ActivityTestRule<>(AdminHomeActivity.class);

    @Test
    public void TestCreateButton() {
        // Type text and then press the button.
        onView(withId(R.id.createUserButton)).perform(click());

        intended(hasComponent(CreateUserActivity.class.getName()));
    }
}

class UserStory1TestCreateUserPage {

    public ActivityTestRule<CreateUserActivity> adminIntentRule =
            new ActivityTestRule<>(CreateUserActivity.class);

    @Test
    public void TestChangeText() {
        // Type text and then press the button.
        onView(withId(R.id.userFirstName))
                .perform(typeText("Minyi"), closeSoftKeyboard());
        onView(withId(R.id.userLastName))
                .perform(typeText("Lu"), closeSoftKeyboard());
        onView(withId(R.id.userEmail))
                .perform(typeText("minyi.lu@gatech.edu"), closeSoftKeyboard());
        onView(withId(R.id.createUserButton)).perform(click());

        intended(hasComponent(AdminHomeActivity.class.getName()));
    }
}
