package edu.gatech;

import org.testng.annotations.*;
import org.testng.Assert;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;

import java.util.List;


public class FrontEndTest 
{
    private WebDriver driver;
    private String baseUrl = "http://localhost:3000/#";
    
    @BeforeTest
    public void setup(){
        String driverPath;
        String os = System.getProperty("os.name");
        if (os.startsWith("Mac")) {
            driverPath = "./src/main/resources/chromedriver-mac";
        } else if (os.startsWith("Linux")) {
            driverPath = "./src/main/resources/chromedriver-linux";
        } else {
            driverPath = "./src/main/resources/chromedriver-windows";
        }
        System.setProperty("webdriver.chrome.driver", driverPath);

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--disable-web-security");
        // options.addArguments("headless");
        driver = new ChromeDriver(options);
        System.out.println("gagagaga");
    }

    @AfterTest
    public void teardown() {
        driver.close();
    }

    @Test
    public void loginAdminTest() throws Exception {
        login("admin");
        
        String expected = baseUrl + "/admin";
        String actual = driver.getCurrentUrl();

        Assert.assertEquals(actual, expected);
    }

    private void login(String email_str) throws Exception {
        driver.get(baseUrl);

        WebElement username = driver.findElement(By.id("user-login-input"));
        WebElement login_btn = driver.findElement(By.id("login-btn"));
        username.sendKeys(email_str);
        login_btn.click();
    }

    private void createUser(String firstname, String lastname, String email_str) throws Exception {
        login("admin");
        //create a user without a project
        WebElement fname = driver.findElement(By.id("fname-input"));
        WebElement lname = driver.findElement(By.id("lname-input"));
        WebElement email = driver.findElement(By.id("email-input"));
        fname.sendKeys(firstname);
        lname.sendKeys(lastname);
        email.sendKeys(email_str);

        WebElement create = driver.findElement(By.id("create-user-btn"));
        create.click();
        Thread.sleep(100);
        driver.switchTo().alert().accept();
    }

    private void deleteUser(String firstname, String lastname, String email_str) throws Exception {
        login("admin");
        
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
        Thread.sleep(100);
    }

    private void checkUserExisting(String email_str) throws Exception {
        login("admin");

        WebElement drop = driver.findElement(By.id("delete-email-select"));
        drop.click();
        Thread.sleep(100);
        List<WebElement> list = driver.findElements(By.tagName("li"));
        for (WebElement e : list) {
            Assert.assertNotEquals(email_str, e.getText());
        }
    }

    private void deleteUserWithProject(String firstname, String lastname, String email_str) throws Exception {
        login("admin");
        deleteUser(firstname, lastname, email_str);

        //cancel the delete
        Thread.sleep(100);
        Alert alert1 = driver.switchTo().alert();
        alert1.dismiss();

        //confirm the delete
        Thread.sleep(100);
        deleteUser(firstname, lastname, email_str);
        Thread.sleep(100);
        Alert alert2 = driver.switchTo().alert();
        alert2.accept();
    }

    private void createProject(String email_str, String projName) throws Exception {
        //create a user without a project
        login(email_str);

        WebElement projectNameInput = driver.findElement(By.id("project-input"));
        projectNameInput.sendKeys(projName);
        WebElement projCreateBtn = driver.findElement(By.id("project-create-btn"));
        projCreateBtn.click();
    }
    private void createSession(String projName) throws Exception {
        WebElement drop = driver.findElement(By.id("existing-projects-select"));
        drop.click();
        Thread.sleep(100);
        List<WebElement> list = driver.findElements(By.tagName("li"));
        for (WebElement e : list) {
            if (e.getText().equals(projName)) {
                e.click();
                break;
            }
        }

        WebElement sessionBtn = driver.findElement(By.id("create-session-btn"));
        sessionBtn.click();
    }

    private void deleteProjectWithSession(String projName, boolean isAccept) throws Exception {
        WebElement drop = driver.findElement(By.id("existing-projects-select"));
        drop.click();
        Thread.sleep(300);
        List<WebElement> list = driver.findElements(By.tagName("li"));
        for (WebElement e : list) {
            if (e.getText().equals(projName)) {
                e.click();
                break;
            }
        }

        WebElement delete = driver.findElement(By.id("delete-project-btn"));
        delete.click();
        
        Thread.sleep(100);
        WebElement btnToClick;
        if(isAccept) {
            btnToClick = driver.findElement(By.id("dialog-accept"));
        } else {
            btnToClick = driver.findElement(By.id("dialog-cancel"));
        }
        btnToClick.click();
        Thread.sleep(100);

        if(isAccept) {
            Alert alert = driver.switchTo().alert();
            alert.accept();
            Thread.sleep(100);
        }

    }

    private boolean checkProjects(String projName) throws Exception {
        WebElement drop = driver.findElement(By.id("existing-projects-select"));
        drop.click();
        Thread.sleep(100);
        List<WebElement> list = driver.findElements(By.tagName("li"));
        for (WebElement e : list) {
            if (e.getText().equals(projName)) {
                return true;
            }
        }
        return false;
    }
}
