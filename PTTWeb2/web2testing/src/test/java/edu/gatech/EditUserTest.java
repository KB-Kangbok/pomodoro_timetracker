package edu.gatech;

import org.testng.annotations.*;
import org.testng.Assert;

import java.util.*;


public class EditUserTest extends BrowserFunctions {
    private final String ADMIN = "admin";
    private final String FIRST_NAME = "Heejoo";
    private final String LAST_NAME = "Cho";
    private final String USERNAME = "test@gatech.edu";
    private final String CHANGE = "change";
    private final String[] SUCCESSFUL_UPDATE = new String[]{"User \"", "\" is successfully edited."};
    private Map<String, String> information;

    @Test(description = "Test to edit first-name for a user")
    public void editFirstName() throws Exception {
        information.put("firstName", CHANGE);
    
        String expected = SUCCESSFUL_UPDATE[0] + USERNAME + SUCCESSFUL_UPDATE[1];
        String actual = utils.editUser(information);
        Assert.assertEquals(actual, expected);
    }

    @Test(description = "Test to edit last-name for a user")
    public void editLastName() throws Exception {
        information.put("lastName", CHANGE);
    
        String expected = SUCCESSFUL_UPDATE[0] + USERNAME + SUCCESSFUL_UPDATE[1];
        String actual = utils.editUser(information);
        Assert.assertEquals(actual, expected);
    }

    @Test(description = "Test to edit first-name and last-name for a user")
    public void editFirstLastName() throws Exception {
        information.put("firstName", CHANGE);
        information.put("lastName", CHANGE);
    
        String expected = SUCCESSFUL_UPDATE[0] + USERNAME + SUCCESSFUL_UPDATE[1];
        String actual = utils.editUser(information);
        Assert.assertEquals(actual, expected);
    }

    @Test(description = "Test to edit with no change")
    public void editNoChange() throws Exception {
        String expected = SUCCESSFUL_UPDATE[0] + USERNAME + SUCCESSFUL_UPDATE[1];
        String actual = utils.editUser(information);
        Assert.assertEquals(actual, expected);
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
    public void clean() throws Exception {
        utils.deleteUser(USERNAME, true);
        utils.logout();
    }

}
