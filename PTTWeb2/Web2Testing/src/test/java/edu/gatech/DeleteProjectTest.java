package edu.gatech;

import org.testng.annotations.*;
import org.testng.Assert;

public class DeleteProjectTest extends BrowserFunctions {
    @Test
    public void deleteProject() throws Exception {
        utils.createUser("kb", "lee", "kb@gmail.com");

        utils.createProject("kb@gmail.com", "exampleProject");
        utils.deleteProject("exampleProject", true);

        boolean isProj = utils.checkProjects("exampleProject");
        utils.deleteUser("kb@gmail.com", true);
        Assert.assertTrue(!isProj);
    }

    @Test
    public void deleteProjectWithSessionCancel() throws Exception {
        utils.createUser("kb", "lee", "kb@gmail.com");

        utils.createProject("kb@gmail.com", "exampleProject");
        utils.createSession("exampleProject");
        utils.deleteProject("exampleProject", false);
        boolean isProj = utils.checkProjects("exampleProject");
        utils.deleteUser("kb@gmail.com", true);
        Assert.assertTrue(isProj);
    }

    @Test
    public void deleteProjectWithSessionAccept() throws Exception {
        utils.createUser("kb", "lee", "kb@gmail.com");

        utils.createProject("kb@gmail.com", "exampleProject");
        utils.createSession("exampleProject");
        utils.deleteProject("exampleProject", true);
        boolean isProj = utils.checkProjects("exampleProject");
        utils.deleteUser("kb@gmail.com", true);
        Assert.assertTrue(!isProj);
    }
}
