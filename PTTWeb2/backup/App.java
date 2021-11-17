package test.java;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

public class App {
    public static void main(String[] args) throws Exception {
        String os = System.getProperty("os.name");
        if (os.startsWith("Mac")) {
            System.setProperty("webdriver.chrome.driver", "./Web2Testing/chromedriver-mac");
        } else if (os.startsWith("Linux")) {
            System.setProperty("webdriver.chrome.driver", "./Web2Testing/chromedriver-linux");
        } else {
            System.setProperty("webdriver.chrome.driver", "./Web2Testing/chromedriver-windows");
        }
        WebDriver driver = new ChromeDriver();
        driver.get("https://www.google.com");

        driver.quit();
    }
}