import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

public class App {
    public static void main(String[] args) throws Exception {
        System.setProperty("webdriver.chrome.driver", "./Web2Testing/chromedriver-windows");
        WebDriver driver = new ChromeDriver();
        driver.get("https://www.google.com");

        driver.quit();
    }
}