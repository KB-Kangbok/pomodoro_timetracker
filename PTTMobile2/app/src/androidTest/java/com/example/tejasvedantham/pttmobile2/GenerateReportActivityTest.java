package com.example.tejasvedantham.pttmobile2;

import static androidx.test.core.app.ApplicationProvider.getApplicationContext;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.action.ViewActions.swipeUp;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.PickerActions.setTime;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.isNotChecked;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static com.example.tejasvedantham.pttmobile2.GenerateReportStep2Activity.formatCompletedPomodoros;
import static com.example.tejasvedantham.pttmobile2.GenerateReportStep2Activity.formatSession;
import static com.example.tejasvedantham.pttmobile2.GenerateReportStep2Activity.formatTotalHoursWorkedOnProject;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.core.StringContains.containsString;

import android.content.Intent;
import android.util.Log;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.espresso.intent.Intents;
import androidx.test.rule.ActivityTestRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.concurrent.TimeUnit;

@RunWith(AndroidJUnit4.class)
public class GenerateReportActivityTest extends TestBase {

    private static final String LOG_TAG = GenerateReportActivityTest.class.getSimpleName();

    @Rule
    public final ActivityTestRule<GenerateReportStep1Activity> reportStep1Rule = new ActivityTestRule<>(GenerateReportStep1Activity.class, false, false);

    @Rule
    public final ActivityTestRule<GenerateReportStep2Activity> reportStep2Rule = new ActivityTestRule<>(GenerateReportStep2Activity.class, false, false);

    private Intent intent;
    private ArrayList<Session> testSessionList = new ArrayList<>();
    private User user;
    private Project project;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        Intents.init();

        testSessionList.add(new Session("2021-11-22T13:00Z", "2021-11-22T13:30Z", 1, null, 0.5));
        testSessionList.add(new Session("2021-11-22T14:00Z", "2021-11-22T15:00Z", 2, null, 1));
        testSessionList.add(new Session("2021-11-22T15:10Z", "2021-11-22T15:40Z", 1, null, 0.5));

        removeAllCurrent();
        TimeUnit.SECONDS.sleep(1);

        user = new User("Yuyi", "Qu", "quyuyi@test.com", "null");
        user.id = createUser(user);

        project = new Project("myproject", null);
        project.id = createProject(user.id, project);

        for (Session session : testSessionList) {
            String sessionId = createSession(user.id, project.id, session);
            session.id = sessionId;
        }

        intent = new Intent();
        intent.putExtra("userId", user.id);
        intent.putExtra("project", project);
        reportStep1Rule.launchActivity(intent);
    }

    @After
    public void tearDown() {
        Intents.release();
    }
    /** Test report when no sessions in time range. */
    @Test
    public void TestNoSessionInTimeRange() {
        onView(withId(R.id.startPicker)).perform(scrollTo(), setTime(12, 0));
        onView(withId(R.id.endPicker)).perform(scrollTo(), setTime(12, 30));
        HashSet<Integer> exptectedSessionIndex = new HashSet<>();

        onView(withId(R.id.generateReportButton)).perform(scrollTo(), click());

        intended(hasComponent(GenerateReportStep2Activity.class.getName()));
        validateReport(exptectedSessionIndex, false, false);
    }
    /** Test report when there are sessions in time range. */
    @Test
    public void TestSessionsReturnedInTimeRange() {
        onView(withId(R.id.startPicker)).perform(scrollTo(), setTime(13, 10));
        onView(withId(R.id.endPicker)).perform(scrollTo(), setTime(14, 50));
        HashSet<Integer> exptectedSessionIndex = new HashSet<>();
        // if a timeframe does not fully contain a session, the system should still include the whole session
        exptectedSessionIndex.add(0);
        exptectedSessionIndex.add(1);

        onView(withId(R.id.generateReportButton)).perform(scrollTo(), click());

        intended(hasComponent(GenerateReportStep2Activity.class.getName()));
        validateReport(exptectedSessionIndex, false, false);
    }
    /** Test report when there sessions in time range and completed pomodors option is selected. */
    @Test
    public void TestIncludeCompletedPomodorosOption() {
        onView(withId(R.id.startPicker)).perform(scrollTo(), setTime(13, 10));
        onView(withId(R.id.endPicker)).perform(scrollTo(), setTime(14, 50));
        HashSet<Integer> exptectedSessionIndex = new HashSet<>();
        // if a timeframe does not fully contain a session, the system should still include the whole session
        exptectedSessionIndex.add(0);
        exptectedSessionIndex.add(1);

        onView(withId(R.id.completedPomodorosCheckbox)).check(matches(isNotChecked())).perform(scrollTo(), click());

        onView(withId(R.id.generateReportButton)).perform(scrollTo(), click());

        intended(hasComponent(GenerateReportStep2Activity.class.getName()));
        validateReport(exptectedSessionIndex, true, false);
    }
    /** Test report when there are sessions in time range and total hours worked on project is selected. */
    @Test
    public void TestIncludeTotalHoursWorkedOnProjectOption() {
        onView(withId(R.id.startPicker)).perform(scrollTo(), setTime(13, 10));
        onView(withId(R.id.endPicker)).perform(scrollTo(), setTime(14, 50));
        HashSet<Integer> exptectedSessionIndex = new HashSet<>();
        // if a timeframe does not fully contain a session, the system should still include the whole session
        exptectedSessionIndex.add(0);
        exptectedSessionIndex.add(1);

        onView(withId(R.id.totalHoursCheckbox)).check(matches(isNotChecked())).perform(scrollTo(), click());

        onView(withId(R.id.generateReportButton)).perform(scrollTo(), click());

        intended(hasComponent(GenerateReportStep2Activity.class.getName()));
        validateReport(exptectedSessionIndex, false, true);
    }

    private void validateReport(HashSet<Integer> exptectedSessionIndex, boolean completedPomodoros, boolean totalHoursWorkedOnProject) {
        int expectedCompletedPomodoros = 0;
        double expectedTotalHoursWorkedOnProject = 0;
        for (int i : exptectedSessionIndex) {
            expectedCompletedPomodoros += testSessionList.get(i).counter;
            expectedTotalHoursWorkedOnProject += testSessionList.get(i).hoursWorked;
        }

        // validate projectNameText
        onView(withId(R.id.projectNameText)).check(matches(withText(containsString(project.projectName))));


        // validate informationText
        for (int i = 0; i < testSessionList.size(); ++i) {
            if (exptectedSessionIndex.contains(i)) {
                onView(withId(R.id.informationText)).check(matches(withText(containsString(formatSession(testSessionList.get(i))))));
            } else {
                onView(withId(R.id.informationText)).check(matches(not(withText(containsString(formatSession(testSessionList.get(i)))))));
            }
        }

        if (completedPomodoros) {

        } else {
            onView(withId(R.id.informationText)).check(matches(not(withText(containsString(formatCompletedPomodoros(expectedCompletedPomodoros))))));
        }

        if (totalHoursWorkedOnProject) {

        } else {
            onView(withId(R.id.informationText)).check(matches(not(withText(containsString(formatTotalHoursWorkedOnProject(expectedCompletedPomodoros))))));
        }
    }
}
