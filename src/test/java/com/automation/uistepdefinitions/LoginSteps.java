package com.automation.uistepdefinitions;

import com.automation.uipages.LoginPage;
import com.automation.utilities.DriverManager;
import io.cucumber.java.en.*;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import org.openqa.selenium.WebDriver;

public class LoginSteps {

    WebDriver driver = DriverManager.getDriver();
    LoginPage loginPage = new LoginPage(driver);

    // User on login page
    @Given("User is on the login page")
    public void userIsOnTheLoginPage() {
        loginPage.navigateToLoginPage();
    }

    // User enters username
    @When("User enters username {string}")
    public void userEntersUsername(String username) {
        loginPage.enterUsername(username);
    }

    // User enters password
    @When("User enters password {string}")
    public void userEntersPassword(String password) {
        loginPage.enterPassword(password);
    }

    // User click login
    @When("User clicks on login button")
    public void userClicksOnLoginButton() {
        loginPage.clickLoginButton();
    }

    // Step that handles both successful and unsuccessful login scenarios
    @Then("User should see {string}")
    public void userShouldSee(String result) {
        if (result.equals("Dashboard")) {
            userShouldBeRedirectedToDashboard();
        } else {
            userShouldSeeErrorMessage();
        }
    }

    // User is on dashboard
    @Then("User should be redirected to dashboard")
    public void userShouldBeRedirectedToDashboard() {
        boolean isOnDashboard = loginPage.isOnDashboard();
        assertTrue(isOnDashboard, "User sees dashboard!");
        System.out.println("User sees dashboard");
    }

    // User sees successful login message
    @Then("User should see welcome message")
    public void userShouldSeeWelcomeMessage() {
        String welcomeMessage = loginPage.getWelcomeMessage();
        assertFalse(welcomeMessage.isEmpty(), "Welcome message is empty!");
        assertTrue(welcomeMessage.contains("Logged In Successfully"), "Welcome message is correct!");
    }

    // User sees error message
    @Then("User should see error message")
    public void userShouldSeeErrorMessage() {
        String errorMessage = loginPage.getErrorMessage();
        assertFalse(errorMessage.isEmpty(), "Error message is empty!");
    }
}
