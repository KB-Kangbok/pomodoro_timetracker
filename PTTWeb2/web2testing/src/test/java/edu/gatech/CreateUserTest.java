package edu.gatech;

import org.testng.annotations.*;
import org.testng.Assert;

public class CreateUserTest extends BrowserFunctions {
    private final String ADMIN = "admin";
    private final String FIRST_NAME = "Heejoo";
    private final String LAST_NAME = "Cho";
    private final String USERNAME = "test@gatech.edu";
    private final String[] USERNAME_ALREADY_EXISTS = new String[]{"User with email ", " already exists!"};
    private final String INFORMATION_INSUFFICIENT = "Please fill in all the fields!";

    @Test(description = "Test to create a user with valid information", groups = {"deleteUser"})
    public void createValidUser() throws Exception {

        utils.createUser(FIRST_NAME, LAST_NAME, USERNAME);
        Assert.assertEquals(utils.checkUserExisting(USERNAME), true);
    }

    @Test(description = "Test to create a user with an existing username", groups = {"deleteUser"}, dependsOnMethods = {"createValidUser"})
    public void createDuplicateUser() throws Exception {
    
        String expected = USERNAME_ALREADY_EXISTS[0] + USERNAME + USERNAME_ALREADY_EXISTS[1];
        String actual = utils.createUser(FIRST_NAME, LAST_NAME, USERNAME);

        Assert.assertEquals(actual, expected);
    }

    @Test(description = "Test to create a user without username")
    public void createUserNoUsername() throws Exception {
        String expected = INFORMATION_INSUFFICIENT;
        String actual = utils.createUser(FIRST_NAME, LAST_NAME, "");

        Assert.assertEquals(actual, expected);
        utils.clearInput();
    }

    @Test(description = "Test to create a user without first name")
    public void createUserNoFirstName() throws Exception {
        String expected = INFORMATION_INSUFFICIENT;
        String actual = utils.createUser("", LAST_NAME, USERNAME);

        Assert.assertEquals(actual, expected);
        utils.clearInput();
    }

    @Test(description = "Test to create a user without last name")
    public void createUserNoLastName() throws Exception {
        String expected = INFORMATION_INSUFFICIENT;
        String actual = utils.createUser(FIRST_NAME, "", USERNAME);

        Assert.assertEquals(actual, expected);
        utils.clearInput();
    }

    @BeforeClass
    public void loginAsAdmin() throws Exception {
        utils.login(ADMIN);
    }

    @AfterClass
    public void logout() throws Exception {
        utils.logout();
    }

    @AfterGroups("deleteUser")
    public void deleteUser() throws Exception {
        utils.deleteUser(USERNAME, true);
    }    
    





}
