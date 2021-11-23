package edu.gatech;

import org.testng.annotations.*;
import org.testng.Assert;

public class StartPomodoroTest extends BrowserFunctions {

    @Test
    public void createPomodoroNotAssociatedProject() throws Exception {
        String actual = utils.clickStartPomodoro();
        String expected = START_POMODORO;

        Assert.assertEquals(actual, expected);

        utils.clickCancel();
        // Assert.assertTrue(utils.projectExists(PROJECT));
    }

    @Test(groups = {"createProject"}, dependsOnMethods = {"createPomodoroNotAssociatedProject"})
    public void createPomodoAssociatedProject() throws Exception {
        String actual = utils.clickStartPomodoro();
        String expected = START_POMODORO;

        Assert.assertEquals(actual, expected);
        
        utils.clickAccept();
        //pick project

    }

    @Test
    public void createPomodoAssociatedProjectWithoutProject() throws Exception {
        String actual = utils.clickStartPomodoro();
        String expected = START_POMODORO;

        Assert.assertEquals(actual, expected);

        utils.clickAccept();

        actual = utils.getAlertMessage();
        expected = NO_PROJECT_TO_BE_ASSOCIATED;
        Assert.assertEquals(actual, expected);
    }

    @BeforeClass
    public void setup() throws Exception {
        utils.login(ADMIN);
        utils.createUser(FIRST_NAME, LAST_NAME, USERNAME);
        utils.logout();
        utils.login(USERNAME);
        utils.clickUserTab("pomodoro");
    }

    @AfterClass
    public void teardown() throws Exception {
        utils.logout();
        utils.login(ADMIN);
        utils.deleteUser(USERNAME, true);
        utils.logout();
    }

    @BeforeGroups("createProject")
    public void createProject() throws Exception {
        utils.createProject(PROJECT);
    }
}
