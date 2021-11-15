package test.java;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.Wait;

import java.util.List;
import java.util.function.Function;


public class DeleteUserAndCreateProjTest {
    public static void main(String[] args) throws Exception {
        System.setProperty("webdriver.chrome.driver", "/chromedriver-linux");

        ChromeOptions options = new ChromeOptions();
//        options.addArguments("--disable-web-security"); // don't enforce the same-origin policy
//        options.addArguments("--disable-gpu"); // applicable to windows os only
//        options.addArguments("--user-data-dir=C:\\Users\\jzhsi\\chromeTemp"); // applicable to windows os only

        WebDriver driver = new ChromeDriver(options);

        //create two users
        createUser(driver, "Joe", "Doe", "123@gmail.com");
        Thread.sleep(100);
        createUser(driver, "Junzhu", "Xiang", "124@gmail.com");

        //delete one user without project
        deleteUser(driver, "Joe", "Doe", "123@gmail.com");
        Thread.sleep(100);
        //create a project for the user and delete the user
        createProject(driver, "124@gmail.com", "p1");
        deleteUserWithProject(driver, "Junzhu", "Xiang", "124@gmail.com");

        //create a user with a project
        createUser(driver, "Jack", "An", "125@gmail.com");
        createProject(driver, "125@gmail.com", "p2");
        //create a project with existing name
        createProject(driver, "125@gmail.com", "p2");
        Thread.sleep(100);
        driver.switchTo().alert().accept();
        deleteUserWithProject(driver, "Jack", "An", "125@gmail.com");

        //finish
        Thread.sleep(100);
        driver.quit();
    }

    private static void createUser(WebDriver driver, String firstname, String lastname, String email_str) throws Exception {

        driver.get("http://localhost:3000/#/admin");
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
        Thread.sleep(100);
    }

    private static void deleteUserWithProject(WebDriver driver, String firstname, String lastname, String email_str) throws Exception {
        //create a user without a project
        driver.get("http://localhost:3000/#/admin");
        deleteUser(driver, firstname, lastname, email_str);

        //cancel the delete
        Thread.sleep(100);
        Alert alert1 = driver.switchTo().alert();
        alert1.dismiss();

        //confirm the delete
        Thread.sleep(100);
        deleteUser(driver, firstname, lastname, email_str);
        Thread.sleep(100);
        Alert alert2 = driver.switchTo().alert();
        alert2.accept();
    }

    private static void createProject(WebDriver driver, String email_str, String projName) throws Exception {
        //create a user without a project
        driver.get("http://localhost:3000/#");

        WebElement username = driver.findElement(By.id("user-login-input"));
        WebElement login_btn = driver.findElement(By.id("login-btn"));
        username.sendKeys(email_str);
        login_btn.click();

        WebElement projectNameInput = driver.findElement(By.id("project-input"));
        projectNameInput.sendKeys(projName);
        WebElement projCreateBtn = driver.findElement(By.id("project-create-btn"));
        projCreateBtn.click();
    }
}
