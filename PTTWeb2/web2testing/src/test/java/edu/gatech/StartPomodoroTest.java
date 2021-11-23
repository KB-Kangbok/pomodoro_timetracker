package edu.gatech;

import org.testng.annotations.*;
import org.testng.Assert;

public class StartPomodoroTest extends BrowserFunctions {

    @Test
    public void createPomodoroNotAssociatedProject() throws Exception {
        String actual = utils.clickStartPomodoro();
        String expected = START_POMODORO;

        Assert.assertEquals(actual, expected);

        utils.clickButton("dialog-cancel", true);
    }

    @Test(groups = {"createProject"}, dependsOnMethods = {"createPomodoroNotAssociatedProject"})
    public void createPomodoAssociatedProject() throws Exception {
        utils.clickUserTab("project");
        utils.createProject(PROJECT);

        utils.clickUserTab("pomodoro");
        String actual = utils.clickStartPomodoro();
        String expected = START_POMODORO;

        Assert.assertEquals(actual, expected);


        utils.clickButton("dialog-accept", true);
        utils.selectProjectForPomodoro(PROJECT);
    }

    @Test(groups = {"clickOk"})
    public void createPomodoAssociatedProjectWithoutProject() throws Exception {
        String actual = utils.clickStartPomodoro();
        String expected = START_POMODORO;

        Assert.assertEquals(actual, expected);

        utils.clickButton("dialog-accept", true);

        String msg = utils.getDialogMessage("associate-dlg");
        Assert.assertTrue(msg.startsWith(NO_PROJECT_TO_BE_ASSOCIATED));

        utils.clickButton("ok-btn", true);
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
        utils.clickUserTab("project");
        utils.createProject(PROJECT);
    }

    @AfterGroups("clickOk")
    public void clickOk() throws Exception {
        utils.clickButton("ok-btn", true);
    }

    @AfterMethod
    public void clickStopPomodoro() throws Exception {
        utils.clickButton("stop-btn", true);
        utils.clickButton("stop-cancel-btn", false);
    }
}
