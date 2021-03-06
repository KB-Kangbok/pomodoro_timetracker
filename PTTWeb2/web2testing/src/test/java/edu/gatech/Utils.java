package edu.gatech;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.Keys;

import java.util.*;

public class Utils {
    public WebDriver driver;
    private String baseUrl;

    public Utils(WebDriver driver, String baseUrl) {
        this.driver = driver;
        this.baseUrl = baseUrl; 
    }
    public void login(String username) throws Exception {
        driver.get(baseUrl);
        
        WebElement email = driver.findElement(By.id("user-login-input"));
        WebElement login = driver.findElement(By.id("login-btn"));
        email.sendKeys(username);
        login.click();
        Thread.sleep(200);
    }

    public void logout() throws Exception {
        try {
            WebElement logout = driver.findElement(By.id("log-out"));
            logout.click();
        } catch(NoSuchElementException e) {
            return;
        }
        Thread.sleep(200);
    }

    public void activateTestButton() throws Exception{
        WebElement test = driver.findElement(By.id("toggle-test-btn"));
        test.click();
        Thread.sleep(100);
    }

    public String createUser(String firstname, String lastname, String username) throws Exception {
        WebElement fname = driver.findElement(By.id("fname-input"));
        WebElement lname = driver.findElement(By.id("lname-input"));
        WebElement email = driver.findElement(By.id("email-input"));
        if (firstname.length() > 0) fname.sendKeys(firstname);
        if (lastname.length() > 0) lname.sendKeys(lastname);
        if (username.length() > 0) email.sendKeys(username);

        WebElement create = driver.findElement(By.id("create-user-btn"));
        create.click();
        Thread.sleep(300);
        return getAlertMessage();
    }

    public void deleteUser(String username, boolean isAccept) throws Exception {        
        WebElement drop = driver.findElement(By.id("delete-email-select"));
        drop.click();
        Thread.sleep(300);
        List<WebElement> users = driver.findElements(By.tagName("li"));
        for (WebElement user : users) {
            if (user.getText().equals(username)) {
                user.click();
                break;
            }
        }
        WebElement delete = driver.findElement(By.id("delete-user-btn"));
        delete.click();
        Thread.sleep(100);

        if(ExpectedConditions.alertIsPresent().apply(driver) != null) {
            if(isAccept) driver.switchTo().alert().accept();
            else driver.switchTo().alert().dismiss();
        }
    }

    public boolean checkEditedUserInfo(Map<String, String> information) throws Exception {
        WebElement drop = driver.findElement(By.id("edit-email-select"));
        drop.click();
        Thread.sleep(100);
        List<WebElement> users = driver.findElements(By.tagName("li"));
        for (WebElement user : users) {
            if (user.getText().equals(information.get("username"))) {
                user.click();
                break;
            }
        }

        for (String change: information.keySet()) {
            if(change.equals("username")) continue;
            String current = driver.findElement(By.id(change)).getAttribute("value");
            if(!current.equals(information.get(change))) {
                System.out.println(change + " " + information.get(change) + " " + current);
                return false;
            }
        }
        
        return true;
    }
    
    public String editUser(Map<String, String> information) throws Exception {
        WebElement drop = driver.findElement(By.id("edit-email-select"));
        drop.click();
        Thread.sleep(100);
        List<WebElement> users = driver.findElements(By.tagName("li"));
        for (WebElement user : users) {
            if (user.getText().equals(information.get("username"))) {
                user.click();
                break;
            }
        }
        
        for (String change: information.keySet()) {
            if(change.equals("username")) continue;
            WebElement element = clearInput(change, "id");
            element.sendKeys(information.get(change));
        }
        
        WebElement edit = driver.findElement(By.id("edit-user-btn"));
        edit.click();
        Thread.sleep(200);

        return getAlertMessage();
    }

    public boolean userExists(String username) throws Exception {
        WebElement drop = driver.findElement(By.id("delete-email-select"));
        drop.click();
        Thread.sleep(100);
        List<WebElement> users = driver.findElements(By.tagName("li"));

        //unclick dropdown
        driver.findElement(By.xpath("//html")).click();

        for (WebElement user : users) {
            if(user.getText().equals(username)) return true;
        } 
        return false;
    }

    public String createProject(String projName) throws Exception {
        Thread.sleep(200);
        WebElement project = driver.findElement(By.id("project-input"));
        project.sendKeys(projName);
        WebElement create = driver.findElement(By.id("project-create-btn"));
        create.click();
        Thread.sleep(200);
        if (ExpectedConditions.alertIsPresent().apply(driver) != null) {
            return getAlertMessage();
        }
        return "no alert!";
    }

    public void deleteProject(String projName, boolean isAccept) throws Exception {
        WebElement drop = driver.findElement(By.id("existing-projects-select"));
        drop.click();
        Thread.sleep(200);
        List<WebElement> projects = driver.findElements(By.tagName("li"));
        for (WebElement project : projects) {
            if (project.getText().equals(projName)) {
                project.click();
                break;
            }
        }

        WebElement delete = driver.findElement(By.id("delete-project-btn"));
        delete.click();
        Thread.sleep(200);

        if(ExpectedConditions.alertIsPresent().apply(driver) != null) {
            driver.switchTo().alert().accept();
            Thread.sleep(200);
        } else {
            if (isAccept) clickButton("dialog-accept", true);
            else clickButton("dialog-cancel", true);
            Thread.sleep(200);

            if(ExpectedConditions.alertIsPresent().apply(driver) != null) {
                driver.switchTo().alert().accept();
                Thread.sleep(200);
            }
        }
        Thread.sleep(200);
    }

    //tabName: project, pomodoro, report
    public void clickUserTab(String tabName) throws Exception {
        WebElement btn = driver.findElement(By.id(tabName.toLowerCase() + "-btn"));
        btn.click();
        Thread.sleep(100);
    }

    public String clickStartPomodoro() throws Exception {
        WebElement btn = driver.findElement(By.id("start-pomodoro-btn"));
        btn.click();
        Thread.sleep(100);
        return getDialogMessage("associate-dlg");
    }

    public void clickButton(String id, boolean btnShouldExist) throws Exception {
        Thread.sleep(200);
        try {
            WebElement btn = driver.findElement(By.id(id));
            btn.click();
            Thread.sleep(200);
        } catch (Exception e) {
            if (!btnShouldExist) return;
            else throw e;
        }
    }

    public void clickContinueBtn() throws Exception {
        WebElement btn = driver.findElement(By.id("continue-accept"));
        btn.click();
        Thread.sleep(100);
    }

    public String clickStopBtn() throws Exception {
        WebElement btn = driver.findElement(By.id("stop-btn"));
        btn.click();
        Thread.sleep(100);
        return getDialogMessage("partial-pomo-dlg");
    }

    public void clickLogPartialBtn(String id) throws Exception {
        WebElement btn = driver.findElement(By.id(id));
        btn.click();
        Thread.sleep(100);
    }

    public WebElement findContinueDialogMsg() throws Exception {
        WebElement dialog = driver.findElement(By.id("continue-dlg"));
        return dialog;
    }

    public void selectProjectForPomodoro(String projName) throws Exception {
        WebElement select = driver.findElement(By.id("projList"));
        select.click();
        List<WebElement> projects = driver.findElements(By.tagName("li"));
        for (WebElement project : projects) {
            if (project.getText().equals(projName)) {
                project.click();
                break;
            }
        }
        WebElement start = driver.findElement(By.id("project-start-btn"));
        Thread.sleep(100);
        start.click();
        Thread.sleep(200);
    }

    public boolean projectExists(String projName) throws Exception {
        WebElement drop = driver.findElement(By.id("existing-projects-select"));
        drop.click();
        Thread.sleep(100);
        List<WebElement> projects = driver.findElements(By.tagName("li"));

        //unclick dropdown
        driver.findElement(By.xpath("//html")).click();

        for (WebElement project : projects) {
            if (project.getText().equals(projName)) {
                project.click();
                return true;
            }
        }
        driver.findElement(By.id("project-input"));
        return false;
    }

    public boolean checkForTimer() throws Exception {
        try {
            driver.findElement(By.id("timer-present"));
            return true;
        } catch(NoSuchElementException e) {
            return false;
        }
    }

    public WebElement clearInput(String inputId, String idType) throws Exception {
        WebElement input;
        if(idType.equals("id")) {
            input = driver.findElement(By.id(inputId));
        } else {
            input = driver.findElement(By.xpath(inputId));
        }
        String os = System.getProperty("os.name");
        if(os.startsWith("Mac")) {
            input.sendKeys(Keys.COMMAND + "a");
        } else {
            input.sendKeys(Keys.CONTROL + "a");
        }
        input.sendKeys(Keys.DELETE);

        return input;
    }

    public String getFirstName() throws Exception {
        WebElement greeting = driver.findElement(By.id("greeting")); //"Hi, FIRSTNAME"
        return greeting.getText().substring(4);
    }

    public String getAlertMessage() throws Exception {
        Thread.sleep(200);
        if(ExpectedConditions.alertIsPresent().apply(driver) != null) {
            Alert alert = driver.switchTo().alert();
            String message = alert.getText();
            alert.accept();
            Thread.sleep(200);
            return message;
        } else {
            return "\"NO ALERT FOUND\"";
        }
    }

    public String getDialogMessage(String id) throws Exception {
        WebElement dialog = driver.findElement(By.id(id));
        Thread.sleep(200);
        return dialog.getText();
    }

    public void selectProject(String selectId, String target) throws Exception {
        WebElement drop = driver.findElement(By.id(selectId));
        drop.click();
        Thread.sleep(100);
        List<WebElement> projects = driver.findElements(By.tagName("li"));
        for (WebElement project : projects) {
            if (project.getText().equals(target)) {
                project.click();
            }
        }
        //unclick dropdown
        driver.findElement(By.xpath("//html")).click();
        Thread.sleep(200);
    }

    public void changeDate(String xpath1, String xpath2, String xpath3, String xpath4, String target) throws Exception {
        driver.findElement(By.xpath(xpath1)).click();
        Thread.sleep(200);
        driver.findElement(By.xpath(xpath2)).click();
        Thread.sleep(200);
        WebElement input = clearInput(xpath3, "xpath");
        input.click();
        input.sendKeys(target);
        Thread.sleep(200);
        driver.findElement(By.xpath(xpath4)).click();
        Thread.sleep(200);
    }

    public void clickCheckbox(String id) throws Exception {
        WebElement checkbox = driver.findElement(By.id(id));
        checkbox.click();
        Thread.sleep(100);
    }

    public int sessionCount() throws Exception {
        List<WebElement> elements = driver.findElements(By.className("results"));
        return elements.size();
    }

    public boolean isOptionReportExist(String type) throws Exception {
        String id = type + "-report";
        try {
            driver.findElement(By.id(id));
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}