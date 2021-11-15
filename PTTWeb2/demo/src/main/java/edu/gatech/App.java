package edu.gatech;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.Alert;

import java.util.List;
/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) throws Exception
    {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--disable-web-security");
        WebDriver driver = new ChromeDriver(options);

        createUser(driver, "KB", "Lee", "kb@gmail.com");
        Thread.sleep(100);
        createProject(driver, "kb@gmail.com", "newProject");
        Thread.sleep(100);
        
        deleteProject(driver, "newProject");
        Thread.sleep(100);
        
        createProject(driver, "kb@gmail.com", "secondProject");
        Thread.sleep(100);
        
        createSession(driver, "secondProject");
        Thread.sleep(100);

        deleteProjectWithSession(driver, "secondProject", false);
        Thread.sleep(100);
        // System.out.println(checkProjects(driver, "secondProject"));
        deleteProjectWithSession(driver, "secondProject", true);
        Thread.sleep(100);
        deleteUser(driver, "kb@gmail.com");
        Thread.sleep(100);
        driver.quit();
    }
    private static void login(WebDriver driver, String email_str) throws Exception {
        driver.get("http://localhost:3000/#");

        WebElement username = driver.findElement(By.id("user-login-input"));
        WebElement login_btn = driver.findElement(By.id("login-btn"));
        username.sendKeys(email_str);
        login_btn.click();
    }

    private static void createUser(WebDriver driver, String firstname, String lastname, String email_str) throws Exception {
        login(driver, "admin");
        //create a user without a project
        WebElement fname = driver.findElement(By.id("fname-input"));
        WebElement lname = driver.findElement(By.id("lname-input"));
        WebElement email = driver.findElement(By.id("email-input"));
        fname.sendKeys(firstname);
        lname.sendKeys(lastname);
        email.sendKeys(email_str);

        WebElement create = driver.findElement(By.id("create-user-btn"));
        create.click();
        Thread.sleep(200);
        driver.switchTo().alert().accept();
    }

    private static void deleteUser(WebDriver driver, String email_str) throws Exception {
        //create a user without a project
        login(driver, "admin");
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

    private static void createProject(WebDriver driver, String email_str, String projName) throws Exception {
        //create a user without a project
        login(driver, email_str);

        WebElement projectNameInput = driver.findElement(By.id("project-input"));
        projectNameInput.sendKeys(projName);
        WebElement projCreateBtn = driver.findElement(By.id("project-create-btn"));
        projCreateBtn.click();
    }

    private static void deleteProject(WebDriver driver, String projName) throws Exception {
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

        WebElement delete = driver.findElement(By.id("delete-project-btn"));
        delete.click();
        
        Thread.sleep(100);
        Alert alert = driver.switchTo().alert();
        alert.accept();
        Thread.sleep(100);

    }

    private static void createSession(WebDriver driver, String projName) throws Exception {
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

    private static void deleteProjectWithSession(WebDriver driver, String projName, boolean isAccept) throws Exception {
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

    private static boolean checkProjects(WebDriver driver, String projName) throws Exception {
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
