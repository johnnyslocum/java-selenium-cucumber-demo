package com.automation.runners;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(

        // Feature files location
        features = "src/test/resources/features",

        // Step definitions and hooks location
        glue = {"com.automation.stepdefinitions", "com.automation.hooks"},

        // Reports generation
        plugin = {
                "pretty",  // make it look good
                "html:target/cucumber-reports/report.html",  // HTML report
                "json:target/cucumber-reports/report.json"   // JSON report
        },
        // set dryRun to true for syntax check only
        dryRun = false,
        monochrome = true,
        tags = "@LoginTest"
)
public class TestRunner {
}
