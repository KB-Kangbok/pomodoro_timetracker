package edu.gatech;

import org.testng.annotations.*;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;


public class BrowserFunctions {
    public static WebDriver driver;
    public static Utils utils;
    private static String baseUrl = "http://localhost:3000/#";


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
        driver = new ChromeDriver(options);
        
        utils = new Utils(driver, baseUrl);
    }

    @AfterSuite
    public void closeBrowser() {
        driver.quit();
    }

    public static String getBaseUrl() {
        return baseUrl;
    }
}