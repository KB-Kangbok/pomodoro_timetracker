package edu.gatech;

import org.testng.annotations.*;
import org.testng.Assert;

public class CreateUserTest extends BrowserFunctions {

    @Test(description = "Test to create a user with valid information", groups = {"deleteUser"})
    public void createValidUser() throws Exception {
        utils.createUser(FIRST_NAME, LAST_NAME, USERNAME);

        Assert.assertEquals(utils.userExists(USERNAME), true);
    }

    @Test(description = "Test to create a user with an existing username", groups = {"deleteUser"}, dependsOnMethods = {"createValidUser"})
    public void createDuplicateUser() throws Exception {
        String expected = USER_ALREADY_EXISTS[0] + USERNAME + USER_ALREADY_EXISTS[1];
        String actual = utils.createUser(FIRST_NAME, LAST_NAME, USERNAME);

        Assert.assertEquals(actual, expected);
    }

    // @AfterGroups("deleteUser")
    @AfterClass
    public void deleteUser() throws Exception {
        utils.deleteUser(USERNAME, true);
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
}
