package com.automation.hooks;

import com.automation.utilities.DriverManager;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

public class Hooks {

    // Runs before each scenario to set up everything we need
    @Before
    public void setUp(Scenario scenario) {
        System.out.println("Starting Test: " + scenario.getName());

        // Initialize browser
        DriverManager.getDriver();
    }

    // Runs after each scenario for cleanup and to screenshot fails
    @After
    public void tearDown(Scenario scenario) {
        // Fetch the driver reference cleanly from the ThreadLocal space for THIS thread
        WebDriver currentDriver = DriverManager.getDriver();

        // Test fails, screenshot
        if (scenario.isFailed()) {
            System.out.println("Test FAILED: " + scenario.getName());

            try {
                // Take screenshot using the thread-isolated driver reference
                byte[] screenshot = ((TakesScreenshot) currentDriver)
                        .getScreenshotAs(OutputType.BYTES);
                scenario.attach(screenshot, "image/png", "Failed Screenshot");
                System.out.println("Screenshot Saved");
            } catch (Exception e) {
                System.err.println("Failed to capture screenshot: " + e.getMessage());
            }
        } else {
            System.out.println("Test PASSED: " + scenario.getName());
        }

        // Cleanly close the browser bound strictly to this thread
        DriverManager.closeDriver();
    }
}
