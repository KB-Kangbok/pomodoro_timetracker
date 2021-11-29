package edu.gatech;

import org.testng.annotations.*;
import org.testng.Assert;

public class CreateProjectTest extends BrowserFunctions {

    @Test
    public void createValidProject() throws Exception {
        utils.createProject(PROJECT);
        
        Assert.assertTrue(utils.projectExists(PROJECT));
    }
    
    @Test(dependsOnMethods = {"createValidProject"})
    public void createDuplicateProject() throws Exception {
        String actual = utils.createProject(PROJECT);
        String expected = PROJ_ALREADY_EXISTS[0] + PROJECT + PROJ_ALREADY_EXISTS[1];
        
        Assert.assertEquals(actual, expected);
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
