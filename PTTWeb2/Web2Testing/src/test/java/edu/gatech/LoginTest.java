package edu.gatech;

import org.testng.annotations.*;
import org.testng.Assert;

public class LoginTest extends BrowserFunctions {
    private final String ADMIN = "admin";
    private final String FIRST_NAME = "John";
    private final String LAST_NAME = "Doe";
    private final String USERNAME = "test@gatech.edu";
    private final String USER_NOT_FOUND = "User not found";

    @Test(description = "Test to login as an admin")
    public void loginAdminTest() throws Exception {
        utils.login(ADMIN);
        
        String expected = getBaseUrl() + "/admin";
        String actual = utils.driver.getCurrentUrl();
        
        System.out.println("Login Test Middle");
        Assert.assertEquals(actual, expected);
    }

    @Test(description = "Test to login as a valid user")
    public void loginValidUserTest() throws Exception {
        createUser();
        utils.login(USERNAME);
        
        String expected = FIRST_NAME;
        String actual = utils.getFirstName();

        Assert.assertEquals(actual, expected);
        deleteUser();
    }

    @Test(description = "Test to login as an invalid user - not registered")
    public void loginInvalidUserTest() throws Exception {
        utils.login(USERNAME);
        
        String expected = USER_NOT_FOUND;
        String actual = utils.getAlertMessage();

        Assert.assertEquals(actual, expected);
    }

    public void createUser() throws Exception {
        // utils.login(ADMIN);
        utils.createUser(FIRST_NAME, LAST_NAME, USERNAME);
        utils.logout();
    }

    public void deleteUser() throws Exception {
        // utils.login(ADMIN);
        utils.deleteUser(USERNAME, true);
        utils.logout();
    }

    @AfterMethod
    public void logout() throws Exception {
        utils.logout();
    }





}
