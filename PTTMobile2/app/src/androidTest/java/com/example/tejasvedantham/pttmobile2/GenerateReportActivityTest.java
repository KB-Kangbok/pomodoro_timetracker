package com.example.tejasvedantham.pttmobile2;

import android.content.Intent;
import android.support.test.runner.AndroidJUnit4;

import androidx.test.espresso.intent.Intents;
import androidx.test.rule.ActivityTestRule;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

@RunWith(AndroidJUnit4.class)
public class GenerateReportActivityTest extends TestBase {

    private static final String LOG_TAG = GenerateReportActivityTest.class.getSimpleName();

    @Rule
    public final ActivityTestRule<GenerateReportStep1Activity> reportStep1Rule = new ActivityTestRule<>(GenerateReportStep1Activity.class, false, false);

    @Rule
    public final ActivityTestRule<GenerateReportStep2Activity> reportStep2Rule = new ActivityTestRule<>(GenerateReportStep2Activity.class, false, false);

    private Intent intent;
    private final ArrayList<Session> testSessionList = new ArrayList<>();
    private User user;
    private Project project;

    @BeforeClass
    public void setUpBeforeClass() {
        testSessionList.add(new Session("2021-11-22T13:00Z", "2021-11-22T13:30Z", 1, null));
        testSessionList.add(new Session("2021-11-22T14:00Z", "2021-11-22T15:00Z", 2, null));
        testSessionList.add(new Session("2021-11-22T15:10Z", "2021-11-22T15:40Z", 1, null));

        user = new User("Yuyi", "Qu", "quyuyi@test.com", null);
        user.id = createUser(user);

        project = new Project("myproject", null);
        project.id = createProject(user.id, project);

        for (Session session : testSessionList) {
            String sessionId = createSession(user.id, project.id, session);
            session.id = sessionId;
        }
    }

    @Before
    public void setUp() throws Exception {
        super.setUp();
        Intents.init();

        removeAllCurrent();
        TimeUnit.SECONDS.sleep(1);

        intent.putExtra("project", project);
        intent.putExtra("userId", user.id);
        reportStep1Rule.launchActivity(intent);
    }

    @After
    public void tearDown() {
        Intents.release();
    }

    @Test
    public void TestNoSessionInTimeRange() {

    }

    @Test
    public void TestSessionsReturnedInTimeRange() {

    }

    @Test
    public void TestIncludeCompletedPomodorosOption() {

    }

    @Test
    public void TestIncludeTotalHoursWorkedOnProjectOption() {

    }
}
