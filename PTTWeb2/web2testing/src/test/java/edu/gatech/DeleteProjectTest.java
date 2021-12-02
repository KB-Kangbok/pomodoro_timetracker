package edu.gatech;

import org.testng.annotations.*;
import org.testng.Assert;

public class DeleteProjectTest extends BrowserFunctions {
    
    @Test
    public void deleteProject() throws Exception {
        utils.createProject(PROJECT);
        
        utils.deleteProject(PROJECT, true);

        Assert.assertTrue(!utils.projectExists(PROJECT));
    }

    @Test(dependsOnMethods = {"deleteProject"})
    public void deleteProjectWithSessionCancel() throws Exception {
        utils.createProject(PROJECT);
        utils.clickUserTab("pomodoro");
        utils.clickStartPomodoro();
        utils.clickButton("dialog-accept", true);
        utils.selectProjectForPomodoro(PROJECT);
        Thread.sleep(POMODORO_DURATION);
        // Thread.sleep(2000);
        utils.clickButton("continue-cancel", true);
        Thread.sleep(200);
        utils.clickUserTab("project");
        
        utils.deleteProject(PROJECT, false);

        Assert.assertTrue(utils.projectExists(PROJECT));
    }

    @Test(dependsOnMethods = {"deleteProjectWithSessionCancel"})
    public void deleteProjectWithSessionAccept() throws Exception {
        utils.deleteProject(PROJECT, true);
        Assert.assertTrue(!utils.projectExists(PROJECT));
    }

    @BeforeClass
    public void setup() throws Exception {
        utils.login(ADMIN);
        utils.createUser(FIRST_NAME, LAST_NAME, USERNAME);
        utils.logout();
        utils.login(USERNAME);
    }

    @AfterClass
    public void teardown() throws Exception {
        utils.logout();
        utils.login(ADMIN);
        utils.deleteUser(USERNAME, true);
        utils.logout();
    }
}
