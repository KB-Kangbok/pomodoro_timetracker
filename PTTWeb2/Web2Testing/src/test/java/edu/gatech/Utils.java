package edu.gatech;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.By;

public class Utils {
    public static WebDriver driver;
    private String baseUrl;

    public Utils(WebDriver driver, String baseUrl) {
        Utils.driver = driver;
        this.baseUrl = baseUrl; 
    }
    public void login(String email_str) throws Exception {
        driver.get(baseUrl);

        WebElement username = driver.findElement(By.id("user-login-input"));
        WebElement login_btn = driver.findElement(By.id("login-btn"));
        username.sendKeys(email_str);
        login_btn.click();
    }
}
