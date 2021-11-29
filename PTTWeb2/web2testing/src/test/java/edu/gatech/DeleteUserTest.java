package edu.gatech;

import org.testng.annotations.*;
import org.testng.Assert;

public class DeleteUserTest extends BrowserFunctions{
    
    @Test
    public void deleteUserNoProject() throws Exception{
        utils.deleteUser(USERNAME, true);

        Assert.assertTrue(!utils.userExists(USERNAME));
    }

    @Test
    public void deleteUserWithProjectAccept() throws Exception{
        utils.login(USERNAME);
        utils.createProject(PROJECT);
        utils.login(ADMIN);
        utils.deleteUser(USERNAME, true);
        
        Assert.assertTrue(!utils.userExists(USERNAME));
    }

    @Test(groups = {"deleteUser"})
    public void deleteUserWithProjectCancel() throws Exception{
        utils.login(USERNAME);
        utils.createProject(PROJECT);
        utils.login(ADMIN);
        utils.deleteUser(USERNAME, false);
        
        Assert.assertTrue(utils.userExists(USERNAME));
        utils.deleteUser(USERNAME, true);
    }

    @AfterGroups("deleteUser")
    public void deleteUser() throws Exception {
        utils.deleteUser(USERNAME, true);
    }

    @BeforeMethod
    public void createUser() throws Exception {
        utils.createUser(FIRST_NAME, LAST_NAME, USERNAME);
    }

    @BeforeClass
    public void loginAsAdmin() throws Exception {
        utils.login(ADMIN);
    }

    @AfterClass
    public void logout() throws Exception {
        utils.logout();
    }
}
