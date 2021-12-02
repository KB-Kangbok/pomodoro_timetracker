package edu.gatech;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class EndPartialPomodoroTest extends BrowserFunctions {
    @BeforeClass
    public void setup() throws Exception {
        utils.login(ADMIN);
        utils.createUser(FIRST_NAME, LAST_NAME, USERNAME);
        utils.login(USERNAME);
        utils.createProject(PROJECT);
        utils.clickUserTab("pomodoro");
    }

    @Test(description = "Test to end a pomodoro in progress")
    public void endPartialPomodoro() throws Exception {
        String actual = utils.clickStartPomodoro();
        String expected = START_POMODORO;
        Thread.sleep(100);
        Assert.assertEquals(actual, expected);

        utils.clickButton("dialog-accept", true);
        utils.selectProjectForPomodoro(PROJECT);

        Thread.sleep(POMODORO_DURATION);
        Assert.assertEquals(utils.findContinueDialogMsg().getText(), CONTINUE_POMODORO);

        utils.clickContinueBtn();
        Thread.sleep(POMODORO_DURATION / 3);

        String stopMsg = utils.clickStopBtn();
        Assert.assertEquals(stopMsg, STOP_POMODORO);

        utils.clickLogPartialBtn("stop-accept-btn");
    }

    @AfterClass
    public void teardown() throws Exception {
        utils.login(ADMIN);
        utils.deleteUser(USERNAME, true);
        utils.logout();
    }
}
