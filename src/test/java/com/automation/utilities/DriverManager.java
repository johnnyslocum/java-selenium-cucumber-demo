package com.automation.utilities;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;


public class DriverManager {

    //  make webdriver thread safe for parallel execution
    private static ThreadLocal<WebDriver> driver = new ThreadLocal<>();

    public static WebDriver getDriver() {
        if (driver.get() == null) {
            driver.set(createDriver());
        }
        return driver.get();
    }

    // Create a browser instance
    private static WebDriver createDriver() {

        // get latest ChromeDriver
        WebDriverManager.chromedriver().setup();

        // Configure Chrome options
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");
        options.addArguments("--disable-notifications");

        // Run in headless mode if specified
        if (System.getProperty("headless", "false").equalsIgnoreCase("true")) {
            options.addArguments("--headless=new");
            options.addArguments("--window-size=1920,1080"); // Important for headless stability
        }

        WebDriver webDriver = new ChromeDriver(options);
        System.out.println("Chrome browser launched successfully");
        return webDriver;
    }

    // cleanup a bit
    public static void closeDriver() {
        if (driver.get() != null) {
            driver.get().quit();
            driver.remove();
            System.out.println("Browser closed successfully");
        }
    }
}
