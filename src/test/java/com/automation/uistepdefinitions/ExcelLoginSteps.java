package com.automation.uistepdefinitions;

import com.automation.uipages.LoginPage;
import com.automation.utilities.ExcelUtils;
import com.automation.utilities.DriverManager;
import io.cucumber.java.en.*;
import org.junit.jupiter.api.Assertions;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ExcelLoginSteps {

    private LoginPage getLoginPage() {
        return new LoginPage(DriverManager.getDriver());
    }
    private List<Map<String, String>> excelRows;
    private final List<String> failures = new ArrayList<>();

    @Given("I load users from excel {string} sheet {string}")
    public void loadUsersFromExcel(String relativePath, String sheetName) {
        try {
            File f = new File(System.getProperty("user.dir"), relativePath);
            System.out.println("[ExcelLoginSteps] Attempting to load excel file: " + f.getAbsolutePath());
            excelRows = ExcelUtils.readSheet(f, sheetName);
            System.out.println("[ExcelLoginSteps] Loaded " + excelRows.size() + " rows from " + f.getAbsolutePath());
        } catch (Exception e) {
            throw new RuntimeException("Failed to load excel: " + e.getMessage(), e);
        }
    }

    @When("I run login checks from the excel rows")
    public void runLoginChecksFromExcelRows() {
        failures.clear();
        if (excelRows == null || excelRows.isEmpty()) {
            throw new IllegalStateException("No excel rows loaded. Did you call the Given step?");
        }
        for (Map<String, String> row : excelRows) {
            String username = row.getOrDefault("username", "");
            String password = row.getOrDefault("password", "");
            String expected = row.getOrDefault("expectedResult", "").trim();

            try {
                getLoginPage().navigateToLoginPage();
                getLoginPage().enterUsername(username);
                getLoginPage().enterPassword(password);
                getLoginPage().clickLoginButton();

                // small check: either redirected to dashboard or show error message
                boolean onDashboard = getLoginPage().isOnDashboard();
                boolean hasError = !getLoginPage().getErrorMessage().isEmpty();

                if (expected.equalsIgnoreCase("Dashboard")) {
                    if (!onDashboard) {
                        failures.add("Expected Dashboard for user=" + username + " but not on dashboard");
                    }
                } else if (expected.equalsIgnoreCase("Error message")) {
                    if (!hasError) {
                        failures.add("Expected error for user=" + username + " but found none");
                    }
                } else {
                    // flexible: allow expected to match partial text in error or title
                    if (!onDashboard && !getLoginPage().getErrorMessage().contains(expected) && !getLoginPage().getPageHeading().contains(expected)) {
                        failures.add("Expected '" + expected + "' for user=" + username + " but did not match");
                    }
                }
            } catch (Throwable t) {
                failures.add("Exception for user=" + username + " -> " + t.getMessage());
            } finally {
                // ensure browser reset for next row
                DriverManager.closeDriver();
            }
        }
    }

    @Then("all excel login checks should pass")
    public void allExcelLoginChecksShouldPass() {
        if (!failures.isEmpty()) {
            String report = String.join("\n", failures);
            Assertions.fail("Some excel-driven login checks failed:\n" + report);
        }
    }
}