package edu.gatech;

import org.testng.annotations.*;
import org.checkerframework.checker.units.qual.A;
import org.testng.Assert;

public class DeleteProjectTest extends BrowserFunctions {
    private final String username = "kb@gmail.com";
    private final String projName = "exampleProject";
    
    @BeforeClass
    public void setup() throws Exception {
        utils.createUser("kb", "lee", username);
    }

    @AfterClass
    public void teardown() throws Exception {
        utils.deleteUser(username, true);
    }

    @Test
    public void deleteProject() throws Exception {
        utils.createProject(username, projName);
        utils.deleteProject(projName, true);

        Assert.assertTrue(!utils.checkProjects(projName));
    }

    @Test(dependsOnMethods = {"deleteProject"})
    public void deleteProjectWithSessionCancel() throws Exception {
        utils.createProject(username, projName);
        utils.createSession(projName);
        utils.deleteProject(projName, false);

        Assert.assertTrue(utils.checkProjects(projName));
    }

    @Test(dependsOnMethods = {"deleteProjectWithSessionCancel"})
    public void deleteProjectWithSessionAccept() throws Exception {
        utils.deleteProject(projName, true);
        Assert.assertTrue(!utils.checkProjects(projName));
    }
}
