package com.automation.stepdefinitions;

import com.automation.pages.LoginPage;
import com.automation.utilities.DriverManager;
import io.cucumber.java.en.*;
import org.junit.Assert;
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

    // User is on dashboard
    @Then("User should be redirected to dashboard")
    public void userShouldBeRedirectedToDashboard() {
        boolean isOnDashboard = loginPage.isOnDashboard();
        Assert.assertTrue("User sees dashboard!", isOnDashboard);
        System.out.println("User sees dashboard");
    }

    // User sees successful login message
    @Then("User should see welcome message")
    public void userShouldSeeWelcomeMessage() {
        String welcomeMessage = loginPage.getWelcomeMessage();
        Assert.assertFalse("Welcome message is empty!",
                welcomeMessage.isEmpty());
        Assert.assertTrue("Welcome message is correct!",
                welcomeMessage.contains("Logged In Successfully"));
    }

    // User sees error message
    @Then("User should see error message")
    public void userShouldSeeErrorMessage() {
        String errorMessage = loginPage.getErrorMessage();
        Assert.assertFalse("Error message is empty!",
                errorMessage.isEmpty());
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
}
