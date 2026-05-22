package com.automation.hooks;

import com.automation.utilities.DriverManager;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

public class Hooks {

    WebDriver driver;

    // Runs before each scenario to set up everything we need
    @Before
    public void setUp(Scenario scenario) {

        System.out.println("Starting Test: " + scenario.getName());

        // Initialize browser
        driver = DriverManager.getDriver();
    }

    // Runs after each scenario for cleanup and to screenshot fails
    @After
    public void tearDown(Scenario scenario) {

        // Test fails, screenshot
        if (scenario.isFailed()) {
            System.out.println("Test FAILED: " + scenario.getName());

            // Take screenshot
            byte[] screenshot = ((TakesScreenshot) driver)
                    .getScreenshotAs(OutputType.BYTES);
            scenario.attach(screenshot, "image/png", "Failed Screenshot");
            System.out.println("Screenshot Saved");
        } else {
            System.out.println("Test PASSED: " + scenario.getName());
        }
        // Close browser
        DriverManager.closeDriver();
    }
}
