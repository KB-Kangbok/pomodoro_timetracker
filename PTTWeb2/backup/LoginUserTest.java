package test.java;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.Keys;
// import org.junit.Assert;

import java.util.List;


public class LoginUserTest {
    public static void main(String[] args) throws Exception {
        String os = System.getProperty("os.name");
        if (os.startsWith("Mac")) {
            System.setProperty("webdriver.chrome.driver", "./chromedriver-mac");
        } else if (os.startsWith("Linux")) {
            System.setProperty("webdriver.chrome.driver", "./chromedriver-linux");
        } else {
            System.setProperty("webdriver.chrome.driver", "./chromedriver-windows");
        }
        
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--disable-web-security");
        WebDriver driver = new ChromeDriver(options);
        
        // Login/logout as admin
        login(driver, "admin"); //Test
        logout(driver);


        // Login/logout as valid user - Success
        createUser(driver, "John", "Doe", "123@gmail.com"); //beforeMethod
        login(driver, "123@gmail.com"); //Test
        logout(driver);

        // Login/logout as invalid (not registered) user -Fail
        login(driver, "124@gmail.com"); //Test
        driver.switchTo().alert().accept();

        // createUser: valid first/last name and email - Success
        createUser(driver, "Mark", "Miller", "124@gmail.com"); //Test

        // createUser: email already exists in the system - Fail
        createUser(driver, "Alex", "Andrews", "124@gmail.com"); //Test
        // driver.switchTo().alert().accept();

        // createUser: email is empty - Fail
        createUser(driver, "Alex", "Andrews", "");
        // driver.switchTo().alert().accept();
        clearInput(driver);
        
        // createUser: first/last name is empty - Fail
        createUser(driver, "Alex", "", "125@gmail.com");
        // driver.switchTo().alert().accept();
        Thread.sleep(200);
        clearInput(driver);
        createUser(driver, "", "Andrews", "125@gmail.com");
        // driver.switchTo().alert().accept();
        Thread.sleep(200);
        clearInput(driver);
        
        //afterClass
        deleteUser(driver, "Joe", "Doe", "123@gmail.com");
        deleteUser(driver, "Mark", "Martin", "124@gmail.com");
        driver.quit();
    }

    private static void login(WebDriver driver, String username) throws Exception {
        driver.get("http://localhost:3000/");
        //create a user without a project
        WebElement username_input = driver.findElement(By.id("user-login-input"));
        username_input.sendKeys(username);
        WebElement signin = driver.findElement(By.id("login-btn"));
        signin.click();
        Thread.sleep(200);
    }

    private static void logout(WebDriver driver) throws Exception{
        WebElement logout = driver.findElement(By.id("log-out"));
        logout.click();
        Thread.sleep(200);
    }

    private static void createUser(WebDriver driver, String firstname, String lastname, String email_str) throws Exception {
        driver.get("http://localhost:3000/#/admin");
        //create a user without a project
        WebElement fname = driver.findElement(By.id("fname-input"));
        WebElement lname = driver.findElement(By.id("lname-input"));
        WebElement email = driver.findElement(By.id("email-input"));
        if (firstname.length() > 0) fname.sendKeys(firstname);
        if (lastname.length() > 0) lname.sendKeys(lastname);
        if (email_str.length() > 0) email.sendKeys(email_str);

        WebElement create = driver.findElement(By.id("create-user-btn"));
        create.click();
        Thread.sleep(100);
        driver.switchTo().alert().accept();
        Thread.sleep(200);
    }

    private static void deleteUser(WebDriver driver, String firstname, String lastname, String email_str) throws Exception {
        //create a user without a project
        driver.get("http://localhost:3000/#/admin");
        WebElement drop = driver.findElement(By.id("delete-email-select"));
        drop.click();
        Thread.sleep(100);
        List<WebElement> list = driver.findElements(By.tagName("li"));
        for (WebElement e : list) {
            if (e.getText().equals(email_str)) {
                e.click();
                break;
            }
        }
        WebElement delete = driver.findElement(By.id("delete-user-btn"));
        delete.click();
        Thread.sleep(200);
    }

    private static void clearInput(WebDriver driver) throws Exception {
        Thread.sleep(300);
        List<WebElement> inputs = driver.findElements(By.tagName("input"));
        for (WebElement input : inputs) {
            if (input.isEnabled()) input.clear();
            Thread.sleep(100);
        }
    }

}
