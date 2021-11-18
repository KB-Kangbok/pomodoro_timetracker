package edu.gatech;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.BeforeTest;


public class BrowserFunctions {
    public Utils utils;
    private String baseUrl = "http://localhost:3000/#";

    @BeforeSuite
    public void initializeBrowser() {
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
        WebDriver driver = new ChromeDriver(options);
        
        utils = new Utils(driver, baseUrl);
    }

    @AfterSuite
    public void closeBrowser() {
        System.out.println("Suite over");
        utils.driver.quit();
    }

    public String getBaseUrl() {
        return baseUrl;
    }
}