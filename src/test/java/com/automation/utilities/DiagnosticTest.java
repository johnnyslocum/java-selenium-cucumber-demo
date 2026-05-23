package com.automation.utilities;

import com.oracle.truffle.api.library.ExportMessage;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.junit.jupiter.api.Test;
import java.time.Duration;

public class DiagnosticTest {

    @Test
    public void testBasicNavigation() throws Exception {
        System.out.println("=== Starting Diagnostic Test ===");

//        System.out.println("1. Setting up WebDriverManager...");
//        WebDriverManager.chromedriver().setup();
//        System.out.println("    WebDriverManager setup complete");
//
//        System.out.println("2. Creating Chrome options...");
//        ChromeOptions options = new ChromeOptions();
//        options.addArguments("--start-maximized");
//        options.addArguments("--disable-notifications");
//        options.addArguments("--disable-extensions");
//        // Try without --no-sandbox temporarily
//        // options.addArguments("--no-sandbox");
//        // options.addArguments("--disable-dev-shm-usage");
//        System.out.println("    Chrome options created");
//
//        System.out.println("3. Creating ChromeDriver instance...");
//        WebDriver driver = new ChromeDriver(options);
//        System.out.println("    ChromeDriver instance created");
//
//        System.out.println("4. Setting timeouts...");
//        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(30));
//        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
//        System.out.println("    Timeouts set");
//
//        try {
//            System.out.println("5. Navigating to practice login page...");
//            long startTime = System.currentTimeMillis();
//            driver.get("https://practicetestautomation.com/practice-test-login/");
//            long endTime = System.currentTimeMillis();
//            System.out.println("    Navigation complete in " + (endTime - startTime) + " ms");
//
//            System.out.println("6. Checking current URL...");
//            String currentUrl = driver.getCurrentUrl();
//            System.out.println("    Current URL: " + currentUrl);
//            if (currentUrl.contains("practice-test-login")) {
//                System.out.println("    Correct page loaded!");
//            } else {
//                System.out.println("    Wrong page: " + currentUrl);
//            }
//
//            System.out.println("7. Checking page title...");
//            String title = driver.getTitle();
//            System.out.println("   Page title: " + title);
//
//            Thread.sleep(3000); // Give time to see the browser
//            System.out.println("=== Test Complete ===");
//
//        } catch (Exception e) {
//            System.err.println("ERROR during navigation:");
//            e.printStackTrace();
//            System.out.println("Current URL: " + driver.getCurrentUrl());
//        } finally {
//            System.out.println("8. Closing driver...");
//            driver.quit();
//            System.out.println("    Driver closed");
//        }
    }
}