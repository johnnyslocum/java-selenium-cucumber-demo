package com.automation.hooks;

import com.automation.utilities.DbUtils;
import com.automation.utilities.DriverManager;
import io.cucumber.java.After;
import io.cucumber.java.AfterAll;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Hooks {

    private static final Logger logger = LoggerFactory.getLogger(Hooks.class);

    // Runs before each scenario to set up everything we need
    @Before
    public void setUp(Scenario scenario) {
        System.out.println("Starting Test: " + scenario.getName());
        logger.info("Starting scenario...");

        // Initialize browser
        DriverManager.getDriver();
        logger.info("WebDriver initialized");
    }

    // Runs after each scenario for cleanup and to screenshot fails
    @After
    public void tearDown(Scenario scenario) {
        System.out.println("Test FAILED: " + scenario.getName());
        logger.info("Ending scenario...");
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
                logger.error("Error during scenario teardown: {}", e.getMessage(), e);
            }
        } else {
            System.out.println("Test PASSED: " + scenario.getName());
        }

        // Cleanly close the browser for current thread
        DriverManager.closeDriver();
        logger.info("Browser closed");
    }
    //Runs once ALL scenarios complete (suite-level cleanup) and is static so it can be called globally.
    @AfterAll
    public static void globalTearDown() {
        logger.info("Global @AfterAll: Shutting down all resources...");
        try {
            // Close db connection
            DbUtils.shutdown();
            logger.info("Database connection pool closed");
        } catch (Exception e) {
            logger.error("Error closing database: {}", e.getMessage(), e);
        }
        logger.info("Global teardown complete");
    }
}
