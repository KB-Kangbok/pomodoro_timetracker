package edu.gatech;

import org.testng.annotations.*;
import org.testng.Assert;

public class EndPomodoroTest extends BrowserFunctions {
//User story 7

    @Test(description = "Test to choose to end session after Pomodoro completes", groups = {})
    public void logProjectAfterEnded() throws Exception {
 
    }

    @Test(description = "Test to continue session with another Pomodoro ", groups = {}, dependsOnMethods = {})
    public void beginNewPomodoroInSession() throws Exception {

    }

    @BeforeClass
    public void setup() throws Exception {
        utils.login(ADMIN);
        utils.createUser(FIRST_NAME, LAST_NAME, USERNAME);
        utils.logout();
        utils.login(USERNAME);
        utils.createProject(PROJECT);
        utils.createSession(PROJECT);
    }

    @AfterClass
    public void teardown() throws Exception {
        utils.logout();
        utils.login(ADMIN);
        utils.deleteUser(USERNAME, true);
        utils.logout();
    } 
}
