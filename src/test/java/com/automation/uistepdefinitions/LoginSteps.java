package com.automation.uistepdefinitions;

import com.automation.uipages.LoginPage;
import com.automation.utilities.DriverManager;
import io.cucumber.java.en.*;
import static org.junit.jupiter.api.Assertions.*;
import org.jspecify.annotations.NonNull;

public class LoginSteps {

    private LoginPage getLoginPage() {
        return new LoginPage(DriverManager.getDriver());
    }

    // User on login page
    @Given("User is on the login page")
    public void userIsOnTheLoginPage() {
        getLoginPage().navigateToLoginPage();
    }

    // User enters username
    @When("User enters username {string}")
    public void userEntersUsername(String username) { getLoginPage().enterUsername(username); }

    // User enters password
    @When("User enters password {string}")
    public void userEntersPassword(String password) { getLoginPage().enterPassword(password); }

    // User click login
    @When("User clicks on login button")
    public void userClicksOnLoginButton() { getLoginPage().clickLoginButton(); }

    // Step that handles both successful and unsuccessful login scenarios
    @Then("User should see {string}")
    public void userShouldSee(@NonNull String result) {
        if (result.equals("Dashboard")) {
            userShouldBeRedirectedToDashboard();
        } else {
            userShouldSeeErrorMessage();
        }
    }

    // User is on dashboard
    @Then("User should be redirected to dashboard")
    public void userShouldBeRedirectedToDashboard() {
        boolean isOnDashboard = getLoginPage().isOnDashboard();
        assertTrue(isOnDashboard, "User sees dashboard!");
        System.out.println("User sees dashboard");
    }

    // User sees successful login message
    @Then("User should see welcome message")
    public void userShouldSeeWelcomeMessage() {
        String welcomeMessage = getLoginPage().getWelcomeMessage();
        assertFalse(welcomeMessage.isEmpty(), "Welcome message is empty!");
        assertTrue(welcomeMessage.contains("Logged In Successfully"), "Welcome message is correct!");
    }

    // User sees error message
    @Then("User should see error message")
    public void userShouldSeeErrorMessage() {
        String errorMessage = getLoginPage().getErrorMessage();
        assertFalse(errorMessage.isEmpty(), "Error message is empty!");
    }

    // Username and password fields and login button are visible
    @Then("the username input field should be visible")
    public void usernameFieldIsVisible() {
        boolean isVisible = getLoginPage().isUsernameFieldVisible();
        assertTrue(isVisible, "Username field should be visible!");
        System.out.println("Username field is visible");
    }

    @Then("the password input field should be visible")
    public void passwordFieldIsVisible() {
        boolean isVisible = getLoginPage().isPasswordFieldVisible();
        assertTrue(isVisible, "Password field should be visible!");
        System.out.println("Password field is visible");
    }

    @Then("the login button should be visible")
    public void loginButtonIsVisible() {
        boolean isVisible = getLoginPage().isLoginButtonVisible();
        assertTrue(isVisible, "Login button should be visible!");
        System.out.println("Login button is visible");
    }

    // Login header and instruction text are correct
    @Then("the login page heading should display {string}")
    public void loginHeadingDisplay(String expectedHeading) {
        String heading = getLoginPage().getPageHeading();
        assertEquals(expectedHeading, heading, "Heading should match!");
        System.out.println("Page heading: " + heading);
    }

    @Then("the page should display login instruction text")
    public void pageDisplaysInstructionText() {
        String instructionText = getLoginPage().getInstructionText();
        assertFalse(instructionText.isEmpty(), "Instruction text should be displayed!");
        assertTrue(instructionText.contains("Login"), "Instruction text should contain 'Login'!");
        System.out.println("Instruction text: " + instructionText);
    }

    // Credentials instructions are visibile
    @Then("the page should show correct credentials {string} and {string}")
    public void pageShowsCorrectCredentials(String username, String password) {
        String userCredentialText = getLoginPage().getUserCredentialText();
        String pwCredentialText = getLoginPage().getPwCredentialText();

        assertTrue(userCredentialText.contains(username), "Should show username: " + username);
        assertTrue(pwCredentialText.contains(password), "Should show password: " + password);
        System.out.println("Correct credentials displayed");
    }

    @Then("the username label should display {string}")
    public void usernameLabelDisplay(String expectedLabel) {
        String label = getLoginPage().getUsernameLabel();
        assertEquals(expectedLabel, label, "Username label should match!");
        System.out.println("Username label: " + label);
    }

    @Then("the password label should display {string}")
    public void passwordLabelDisplay(String expectedLabel) {
        String label = getLoginPage().getPasswordLabel();
        assertEquals(expectedLabel, label, "Password label should match!");
        System.out.println("Password label: " + label);
    }

    // User tries logging in without entering credentials
    @When("User clicks on login button without entering credentials")
    public void clickLoginWithoutCredentials() {
        getLoginPage().clickLoginButton();
    }

    // User tries logging in without entering password
    @When("User clicks on login button without password")
    public void clickLoginWithoutPassword() {
        getLoginPage().clickLoginButton();
    }

    // User tries logging in without entering username
    @When("User clicks on login button without username")
    public void clickLoginWithoutUsername() {
        getLoginPage().clickLoginButton();
    }

    // Appropriate error message is displayed
    @Then("User should see appropriate error or validation message")
    public void verifyAppropriateError() {
        String errorMessage = getLoginPage().getErrorMessage();
        assertFalse(errorMessage.isEmpty(), "Error message should be displayed!");
        System.out.println("Error message shown: " + errorMessage);
    }

    // Error message contains expected text
    @Then("the error message should contain {string}")
    public void errorMessageContains(String expectedText) {
        String errorMessage = getLoginPage().getErrorMessage();
        assertTrue(errorMessage.contains(expectedText),
                "Error message should contain: " + expectedText);
        System.out.println("Error message contains expected text: " + expectedText);
    }

    // User clears username field
    @When("User clears username field")
    public void clearUsernameField() {
        getLoginPage().clearUsernameField();
        System.out.println("Username field cleared");
    }

    // User clears password field
    @When("User clears password field")
    public void clearPasswordField() {
        getLoginPage().clearPasswordField();
        System.out.println("Password field cleared");
    }
}

