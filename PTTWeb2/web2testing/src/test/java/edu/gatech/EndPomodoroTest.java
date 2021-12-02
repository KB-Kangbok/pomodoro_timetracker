package edu.gatech;

import org.testng.annotations.*;
import org.testng.Assert;

public class EndPomodoroTest extends BrowserFunctions {
//User story 7

    @Test(description = "Test to choose to end session after Pomodoro completes")
    public void endSessionTest() throws Exception {
        String actual = utils.clickStartPomodoro();
        String expected = START_POMODORO;
        Thread.sleep(100);
        Assert.assertEquals(actual, expected);
        
        utils.clickButton("dialog-accept", true);
        utils.selectProjectForPomodoro(PROJECT);
        
        Thread.sleep(POMODORO_DURATION);
        Assert.assertEquals(utils.findContinueDialogMsg().getText(), CONTINUE_POMODORO);
        
        utils.clickButton("continue-cancel", true);
        Assert.assertTrue(!utils.checkForTimer());
    }
    
    @Test(description = "Test to continue session with another Pomodoro ", dependsOnMethods = {"endSessionTest"})
    public void continueSessionTest() throws Exception {
        String actual = utils.clickStartPomodoro();
        String expected = START_POMODORO;
        Thread.sleep(100);
        Assert.assertEquals(actual, expected);
        
        utils.clickButton("dialog-accept", true);
        utils.selectProjectForPomodoro(PROJECT);
        
        Thread.sleep(POMODORO_DURATION);
        Assert.assertEquals(utils.findContinueDialogMsg().getText(), CONTINUE_POMODORO); 

        utils.clickContinueBtn();
        Assert.assertTrue(utils.checkForTimer());
        Thread.sleep(100);
        utils.clickStopBtn();
        utils.clickLogPartialBtn("stop-accept-btn");
    }

    @BeforeClass
    public void setup() throws Exception {
        utils.login(ADMIN);
        utils.createUser(FIRST_NAME, LAST_NAME, USERNAME);
        utils.logout();
        utils.login(USERNAME);
        utils.clickUserTab("project");
        utils.createProject(PROJECT);
        utils.clickUserTab("pomodoro");
    }

    @AfterClass
    public void teardown() throws Exception {
        utils.logout();
        utils.login(ADMIN);
        utils.deleteUser(USERNAME, true);
        utils.logout();
    } 
}