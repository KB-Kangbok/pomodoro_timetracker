package edu.gatech;

import org.testng.annotations.*;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;


public class BrowserFunctions {
    //dummy strings
    public final String ADMIN = "admin";
    public final String FIRST_NAME = "first";
    public final String LAST_NAME = "last";
    public final String USERNAME = "test@gatech.edu";
    public final String PROJECT = "proj";
    public final String PROJECT2 = "proj2";
    public final String CHANGE = "change";
    public final int POMODORO_DURATION = 10000; //10 seconds
    //alert messages
    public final String USER_NOT_FOUND = "User not found";
    public final String INFORMATION_INSUFFICIENT = "Please fill in all the fields!";
    public final String[] SUCCESSFUL_UPDATE = new String[]{"User \"", "\" is successfully edited."};
    public final String[] USER_ALREADY_EXISTS = new String[]{"User with email ", " already exists!"};
    public final String[] PROJ_ALREADY_EXISTS = new String[]{"Project ", " already exists!"};
    public final String START_POMODORO = "Do you want to associate Pomodoro with project?";
    public final String NO_PROJECT_TO_BE_ASSOCIATED = "You don't have any projects to associate!";
    public final String CONTINUE_POMODORO = "Do you want to continue another pomodoro?";
    public final String STOP_POMODORO = "Do you want to keep partial pomodoro data?";

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