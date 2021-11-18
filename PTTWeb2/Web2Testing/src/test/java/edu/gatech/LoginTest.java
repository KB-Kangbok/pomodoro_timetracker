package edu.gatech;

import org.testng.annotations.*;
import org.testng.Assert;

// import org.openqa.selenium.WebDriver;
// import org.openqa.selenium.chrome.ChromeDriver;
// import org.openqa.selenium.chrome.ChromeOptions;
// import org.openqa.selenium.WebElement;
// import org.openqa.selenium.Alert;
// import org.openqa.selenium.By;

// import edu.gatech.Utils;

public class LoginTest extends BrowserFunctions {

    @Test
    public void loginAdminTest() throws Exception {
        System.out.println("Login Test Start");
        utils.login("admin");
        
        String expected = getBaseUrl() + "/admin";
        String actual = utils.driver.getCurrentUrl();
        
        System.out.println("Login Test Middle");
        Assert.assertEquals(actual, expected);
    }
    

}
