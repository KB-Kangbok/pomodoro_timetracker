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
import org.openqa.selenium.Keys;

import java.util.List;
import java.util.function.Function;


public class EditUserTest {
    public static void main(String[] args) throws Exception {
        // System.setProperty("webdriver.chrome.driver", "./Web2Testing/chromedriver-mac");

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--disable-web-security");
        WebDriver driver = new ChromeDriver(options);

        // edit user - just first name
        createUser(driver, "John", "Doe", "123@gmail.com");
        WebElement drop = driver.findElement(By.id("edit-email-select"));
        drop.click();
        Thread.sleep(100);
        List<WebElement> list = driver.findElements(By.tagName("li"));
        for (WebElement e : list) {
            if (e.getText().equals("123@gmail.com")) {
                e.click();
                break;
            }
        }
        WebElement input = driver.findElement(By.id("edit-fname"));
        input.sendKeys(Keys.chord(Keys.CONTROL,"a", Keys.DELETE));
        // editUser(driver, "Joe", "Doe", "123@gmail.com");
        // Thread.sleep(200);

        // // edit user - just last name
        // createUser(driver, "Mark", "Miller", "124@gmail.com");
        // editUser(driver, "Mark", "Martin", "124@gmail.com");
        // Thread.sleep(200);

        // // edit user - both first and last name
        // createUser(driver, "Alex", "Andrews", "125@gmail.com");
        // editUser(driver, "Andy", "White", "125@gmail.com");
        // Thread.sleep(200);

        // // edit user - both unchanged
        // createUser(driver, "Olivia", "Marcus", "126@gmail.com");
        // editUser(driver, "Olivia", "Marcus", "126@gmail.com");
        // Thread.sleep(200);

        // //finish
        // Thread.sleep(100);
        // driver.quit();
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

    private static void editUser(WebDriver driver, String firstname, String lastname, String email_str) throws Exception {
        //edit user
        driver.get("http://localhost:3000/#/admin");
        WebElement drop = driver.findElement(By.id("edit-email-select"));
        drop.click();
        Thread.sleep(100);
        List<WebElement> list = driver.findElements(By.tagName("li"));
        for (WebElement e : list) {
            if (e.getText().equals(email_str)) {
                e.click();
                break;
            }
        }

        WebElement fname = driver.findElement(By.id("edit-fname"));
        WebElement lname = driver.findElement(By.id("edit-lname"));
        fname.clear();
        lname.clear();

        WebElement edit = driver.findElement(By.id("edit-user-btn"));
        edit.click();
        Thread.sleep(100);

        Alert alert = driver.switchTo().alert();
        alert.accept();
        Thread.sleep(100);
    }
}
