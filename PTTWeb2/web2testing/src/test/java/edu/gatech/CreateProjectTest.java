package edu.gatech;

import org.testng.annotations.*;
import org.testng.Assert;

public class CreateProjectTest extends BrowserFunctions {
    private final String username = "kb@gmail.com";
    private final String projName = "sample";

    @BeforeClass
    public void createUser() throws Exception {
        utils.createUser("kb", "lee", username);
    }

    @AfterClass
    public void deleteUser() throws Exception {
        utils.deleteUser(username, true);
    }

    @Test
    public void createValidProject() throws Exception {
        utils.createProject(username, projName);
        
        Assert.assertTrue(utils.checkProjects(projName));
    }
    
    @Test(dependsOnMethods = {"createValidProject"})
    public void createDuplicateProject() throws Exception {
        utils.createProject(username, projName);
        String actual = utils.getAlertMessage();
        String expected = "Project " + projName + " already exists!";
        Assert.assertEquals(actual, expected);
    }

}
