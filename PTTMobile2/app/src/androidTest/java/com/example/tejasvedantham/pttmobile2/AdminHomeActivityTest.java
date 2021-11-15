package com.example.tejasvedantham.pttmobile2;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static com.example.tejasvedantham.pttmobile2.UserListAdapter.CONFIRM_MSG;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;

@RunWith(AndroidJUnit4.class)
public class AdminHomeActivityTest {

    private static ArrayList<User> testUserList = new ArrayList<>();

    @Before
    private void init() {
        testUserList.add(new User("Saira", "Poonnen", "saira.poonnen@gatech.edu", null));
        testUserList.add(new User("Minyi", "Lu", "minyi.lu@gatech.edu", null));
        testUserList.add(new User("Tejas", "Vedantham", "tejas.vedantham@gatech.edu", null));
        testUserList.add(new User("Yuyi", "Qu", "yuyi.qu@gatech.edu", null));

        for (User user : testUserList) {
            createUserHelper(user);
        }
    }

    @Test
    public void TestDeleteUserWithoutProjects() {
        // setup the test: add some users
        // TODO

        onData(User())
        onView(withId(R.id.deleteUser)).perform(click());

    }

    @Test
    public void TestDeleteUserWithProjects() {
        onView(withId(R.id.deleteUser)).perform(click());
        onView(withText(CONFIRM_MSG)).check(matches(isDisplayed()));

    }
}
