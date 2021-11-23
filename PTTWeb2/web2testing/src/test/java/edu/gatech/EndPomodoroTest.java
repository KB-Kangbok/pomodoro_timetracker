package edu.gatech;

import org.testng.annotations.*;
import org.testng.Assert;

public class EndPomodoroTest extends BrowserFunctions {
//User story 7

    @Test(description = "Test to choose to end session after Pomodoro completes")
    public void endSessionTest() throws Exception {
        utils.clickStartPomodoro();
        utils.selectProjectForPomodoro(PROJECT);

        Thread.sleep(POMODORO_DURATION);
        Assert.assertEquals(utils.findContinueDialogMsg().getText(), CONTINUE_POMODORO);

        utils.clickStopBtn();
        // timer not visible
        // Thread.sleep(POMODORO_DURATION);
    }

    @Test(description = "Test to continue session with another Pomodoro ")
    public void continueSessionTest() throws Exception {
        utils.clickStartPomodoro();
        utils.selectProjectForPomodoro(PROJECT2);
        
        Thread.sleep(POMODORO_DURATION);
        Assert.assertEquals(utils.findContinueDialogMsg().getText(), CONTINUE_POMODORO); 

        utils.clickContinueBtn();
        Thread.sleep(POMODORO_DURATION);

        utils.clickStopBtn();
    }

    @BeforeClass
    public void setup() throws Exception {
        utils.login(ADMIN);
        utils.createUser(FIRST_NAME, LAST_NAME, USERNAME);
        utils.logout();
        utils.login(USERNAME);
        utils.activateTestButton();
        utils.clickUserTab("pomodoro");
        utils.createProject(PROJECT);
        utils.createProject(PROJECT2);
    }

    @AfterClass
    public void teardown() throws Exception {
        utils.logout();
        utils.login(ADMIN);
        utils.deleteUser(USERNAME, true);
        utils.logout();
    } 
}