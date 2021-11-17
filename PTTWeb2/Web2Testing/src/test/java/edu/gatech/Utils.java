package edu.gatech;

import org.testng.Assert;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;

import java.util.List;

public class Utils {
    public WebDriver driver;
    private String baseUrl;

    public Utils(WebDriver driver, String baseUrl) {
        this.driver = driver;
        this.baseUrl = baseUrl; 
    }
    public void login(String email_str) throws Exception {
        driver.get(baseUrl);

        WebElement username = driver.findElement(By.id("user-login-input"));
        WebElement login_btn = driver.findElement(By.id("login-btn"));
        username.sendKeys(email_str);
        login_btn.click();
    }

    public static void logout(WebDriver driver) throws Exception {
        WebElement logout = driver.findElement(By.id("log-out"));
        logout.click();
        Thread.sleep(200);
    }

    public void createUser(String firstname, String lastname, String username) throws Exception {
        login("admin");
        //create a user without a project
        WebElement fname = driver.findElement(By.id("fname-input"));
        WebElement lname = driver.findElement(By.id("lname-input"));
        WebElement email = driver.findElement(By.id("email-input"));
        fname.sendKeys(firstname);
        lname.sendKeys(lastname);
        email.sendKeys(username);

        WebElement create = driver.findElement(By.id("create-user-btn"));
        create.click();
        Thread.sleep(100);
        driver.switchTo().alert().accept();
    }


    public void deleteUser(String firstname, String lastname, String username) throws Exception {
        login("admin");
        
        WebElement drop = driver.findElement(By.id("delete-email-select"));
        drop.click();
        Thread.sleep(100);
        List<WebElement> list = driver.findElements(By.tagName("li"));
        for (WebElement e : list) {
            if (e.getText().equals(username)) {
                e.click();
                break;
            }
        }
        WebElement delete = driver.findElement(By.id("delete-user-btn"));
        delete.click();
        Thread.sleep(100);
    }

    public void checkUserExisting(String username) throws Exception {
        login("admin");

        WebElement drop = driver.findElement(By.id("delete-email-select"));
        drop.click();
        Thread.sleep(100);
        List<WebElement> users = driver.findElements(By.tagName("li"));
        for (WebElement user : users) {
            Assert.assertNotEquals(username, user.getText());
        }
    }

    public void deleteUserWithProject(String firstname, String lastname, String username) throws Exception {
        login("admin");
        deleteUser(firstname, lastname, username);

        //cancel the delete
        Thread.sleep(100);
        Alert alert1 = driver.switchTo().alert();
        alert1.dismiss();

        //confirm the delete
        Thread.sleep(100);
        deleteUser(firstname, lastname, username);
        Thread.sleep(100);
        Alert alert2 = driver.switchTo().alert();
        alert2.accept();
    }

    public void createProject(String username, String projName) throws Exception {
        //create a user without a project
        login(username);

        WebElement projectNameInput = driver.findElement(By.id("project-input"));
        projectNameInput.sendKeys(projName);
        WebElement projCreateBtn = driver.findElement(By.id("project-create-btn"));
        projCreateBtn.click();
    }

    public void createSession(String projName) throws Exception {
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

    public void deleteProjectWithSession(String projName, boolean isAccept) throws Exception {
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

    public boolean checkProjects(String projName) throws Exception {
        WebElement drop = driver.findElement(By.id("existing-projects-select"));
        drop.click();
        Thread.sleep(100);
        List<WebElement> projects = driver.findElements(By.tagName("li"));
        for (WebElement project : projects) {
            if (project.getText().equals(projName)) {
                return true;
            }
        }
        return false;
    }
}
