package edu.gatech;

import org.testng.annotations.*;
import org.testng.Assert;

public class LoginTest extends BrowserFunctions {

    @Test(description = "Test to login as an admin")
    public void loginAdminTest() throws Exception {
        utils.login(ADMIN);
        
        String expected = getBaseUrl() + "/admin";
        String actual = utils.driver.getCurrentUrl();

        Assert.assertEquals(actual, expected);
        utils.logout();
    }

    @Test(description = "Test to login as a non-existing user")
    public void loginInvalidUserTest() throws Exception {
        utils.login("Invalid");
        String expected = USER_NOT_FOUND;
        String actual = utils.getAlertMessage();
        Thread.sleep(100);
        Assert.assertEquals(actual, expected);
        utils.clearInput("user-login-input", "id");
    }

    @Test(description = "Test to login as a valid user", groups = {"validUser"})
    public void loginValidUserTest() throws Exception {
        utils.login(ADMIN);
        utils.createUser(FIRST_NAME, LAST_NAME, USERNAME);
        utils.login(USERNAME);
        
        String expected = FIRST_NAME;
        String actual = utils.getFirstName();
        Thread.sleep(100);
        Assert.assertEquals(actual, expected);

        utils.login(ADMIN);
        utils.deleteUser(USERNAME, true);
    }

    @AfterClass
    public void teardown() throws Exception {
        utils.logout();
    }
}
