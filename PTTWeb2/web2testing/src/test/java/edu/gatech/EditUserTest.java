package edu.gatech;

import org.testng.annotations.*;
import org.testng.Assert;

import java.util.*;


public class EditUserTest extends BrowserFunctions {
    private Map<String, String> information;

    @Test(description = "Test to edit first-name for a user")
    public void editFirstName() throws Exception {
        information.put("edit-fname", CHANGE[0]);
        
        String expected = SUCCESSFUL_UPDATE[0] + USERNAME + SUCCESSFUL_UPDATE[1];
        String actual = utils.editUser(information);
        Assert.assertEquals(actual, expected);
        Assert.assertTrue(utils.checkEditedUserInfo(information));
    }

    @Test(description = "Test to edit last-name for a user", dependsOnMethods = {"editFirstName"})
    public void editLastName() throws Exception {
        information.put("edit-lname", CHANGE[1]);
    
        String expected = SUCCESSFUL_UPDATE[0] + USERNAME + SUCCESSFUL_UPDATE[1];
        String actual = utils.editUser(information);
        Assert.assertEquals(actual, expected);
        Assert.assertTrue(utils.checkEditedUserInfo(information));
    }
    
    @Test(description = "Test to edit first-name and last-name for a user", dependsOnMethods = {"editLastName"})
    public void editFirstLastName() throws Exception {
        information.put("edit-fname", CHANGE[1]);
        information.put("edit-lname", CHANGE[2]);
        
        String expected = SUCCESSFUL_UPDATE[0] + USERNAME + SUCCESSFUL_UPDATE[1];
        String actual = utils.editUser(information);
        Assert.assertEquals(actual, expected);
        Assert.assertTrue(utils.checkEditedUserInfo(information));
    }
    
    @Test(description = "Test to edit with no change")
    public void editNoChange() throws Exception {
        String expected = SUCCESSFUL_UPDATE[0] + USERNAME + SUCCESSFUL_UPDATE[1];
        String actual = utils.editUser(information);
        Assert.assertEquals(actual, expected);
        Assert.assertTrue(utils.checkEditedUserInfo(information));
    }
    
    @BeforeMethod
    public void initInfo() throws Exception {
        information = new HashMap<>();
        information.put("username", USERNAME);
    }

    @BeforeClass
    public void setup() throws Exception {
        utils.login(ADMIN);
        utils.createUser(FIRST_NAME, LAST_NAME, USERNAME);
    }

    @AfterClass
    public void teardown() throws Exception {
        utils.deleteUser(USERNAME, true);
        utils.logout();
    }
}
