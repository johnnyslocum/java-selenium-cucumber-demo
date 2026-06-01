package com.automation.uipages;

import org.openqa.selenium.*;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;
import java.util.Objects;
import com.automation.utilities.DriverManager;
import org.openqa.selenium.support.ui.FluentWait;
import java.util.function.Function;


// page_url = https://practicetestautomation.com/practice-test-login/
public class LoginPage {

    WebDriver driver;
    WebDriverWait wait;

    // Constructor
    public LoginPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        PageFactory.initElements(driver, this);
    }

    // Define locators for the page elements
    @FindBy(xpath = "//h2[contains(text(), 'Test login')]")
    private WebElement pageHeading;

    @FindBy(xpath = "//li[contains(text(), 'This is a simple')]")
    private WebElement instructionText;

    @FindBy(xpath = "//b[contains(text(), 'student')]")
    private WebElement userCredentialText;

    @FindBy(xpath = "//b[contains(text(), 'Password123')]")
    private WebElement pwCredentialText;

    @FindBy(xpath = "//label[contains(text(), 'Username')]")
    private WebElement usernameLabel;

    @FindBy(id = "username")
    private WebElement usernameField;

    @FindBy(xpath = "//label[contains(text(), 'Password')]")
    private WebElement passwordLabel;

    @FindBy(id = "password")
    private WebElement passwordField;

    @FindBy(xpath = "//p[contains(text(), 'Logged In Successfully')]")
    private WebElement dashboardHeading;

    @FindBy(id = "submit")
    private WebElement loginButton;

    @FindBy(id = "error")
    private WebElement errorMessage;

    @FindBy(xpath = "//h1[contains(@class,'post-title')]")
    private WebElement welcomeMessage;

    // Page specific actions
    public boolean isBrowserStillOpen(WebDriver currentDriver) {
        try {
            currentDriver.getTitle(); // Simple check if browser is responsive
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public void navigateToLoginPage() {
        String url = "https://practicetestautomation.com/practice-test-login/";
        WebDriver wd = this.driver; // existing field
        try {
            System.out.println("[LoginPage] Navigating to: " + url);
            // Use get() which will throw exception if pageLoadTimeout is hit
            wd.get(url);

            // Safely maximize (avoids crashes on some environments)
            DriverManager.safeMaximize(wd);

            // Wait for document.readyState == "complete" with a FluentWait
            FluentWait<WebDriver> fWait = new FluentWait<>(wd)
                    .withTimeout(Duration.ofSeconds(30))   // fail-fast page load wait
                    .pollingEvery(Duration.ofMillis(500))
                    .ignoring(Exception.class);

            Boolean ready = fWait.until(new Function<WebDriver, Boolean>() {
                @Override
                public Boolean apply(WebDriver driver) {
                    try {
                        Object result = ((JavascriptExecutor) driver).executeScript("return document.readyState");
                        return result != null && result.toString().equals("complete");
                    } catch (Exception e) {
                        return false;
                    }
                }
            });

            if (!Boolean.TRUE.equals(ready)) {
                System.err.println("[LoginPage] Warning: document.readyState != complete after wait");
            }

            // Log the final URL and title for debugging
            try {
                System.out.println("[LoginPage] Current URL: " + wd.getCurrentUrl());
                System.out.println("[LoginPage] Page title: " + wd.getTitle());
            } catch (Exception e) {
                System.err.println("[LoginPage] Unable to read URL/title: " + e.getMessage());
            }

            System.out.println("[LoginPage] User on login page");
        } catch (TimeoutException te) {
            System.err.println("[LoginPage] ERROR: pageLoadTimeout exceeded while navigating to " + url + " : " + te.getMessage());
            // let caller handle fail (Hooks will screenshot and close)
            throw te;
        } catch (Exception e) {
            System.err.println("[LoginPage] ERROR navigating to login page: " + e.getMessage());
            // Provide current url if available
            try {
                System.err.println("[LoginPage] Current URL (post-failure): " + wd.getCurrentUrl());
            } catch (Exception ex) {
                System.err.println("[LoginPage] Unable to get current URL after failure: " + ex.getMessage());
            }
            throw e;
        }
    }

    public void enterUsername(String username) {
        wait.until(ExpectedConditions.visibilityOf(usernameField));
        usernameField.clear();
        usernameField.sendKeys(username);
        System.out.println("Entered username: " + username);
    }

    public void enterPassword(String password) {
        wait.until(ExpectedConditions.visibilityOf(passwordField));
        passwordField.clear();
        passwordField.sendKeys(password);
        System.out.println("Entered password");
    }

    public void clickLoginButton() {
        wait.until(ExpectedConditions.elementToBeClickable(loginButton));
        loginButton.click();
        System.out.println("Clicked login button");
    }

    public String getErrorMessage() {
        wait.until(ExpectedConditions.visibilityOf(errorMessage));
        String error = errorMessage.getText();
        System.out.println("Error message: " + error);
        return error;
    }

//    public String getErrorMessage2() {
//        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("error")));
//
//        if (errorMessage.isDisplayed()) {
//            String error = errorMessage.getText();
//            System.out.println("Error message captured: " + error);
//            return error;
//        }
//        return "";
//    }

    public String getWelcomeMessage() {
        wait.until(ExpectedConditions.visibilityOf(welcomeMessage));
        String message = welcomeMessage.getText();
        System.out.println("Welcome message: " + message);
        return message;
    }

    public boolean isOnDashboard() {
        wait.until(ExpectedConditions.urlContains("logged-in-successfully"));
        return Objects.requireNonNull(driver.getCurrentUrl()).contains("logged-in-successfully");
    }

    public boolean isUsernameFieldVisible() {
        try {
            wait.until(ExpectedConditions.visibilityOf(usernameField));
            return usernameField.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isPasswordFieldVisible() {
        try {
            wait.until(ExpectedConditions.visibilityOf(passwordField));
            return passwordField.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isLoginButtonVisible() {
        try {
            wait.until(ExpectedConditions.visibilityOf(loginButton));
            return loginButton.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public String getPageHeading() {
        wait.until(ExpectedConditions.visibilityOf(pageHeading));
        return pageHeading.getText();
    }

    public String getInstructionText() {
        wait.until(ExpectedConditions.visibilityOf(instructionText));
        return instructionText.getText();
    }

    public String getUserCredentialText() {
        wait.until(ExpectedConditions.visibilityOf(userCredentialText));
        return userCredentialText.getText();
    }

    public String getPwCredentialText() {
        wait.until(ExpectedConditions.visibilityOf(pwCredentialText));
        return pwCredentialText.getText();
    }

    public String getUsernameLabel() {
        wait.until(ExpectedConditions.visibilityOf(usernameLabel));
        return "Username"; // Based on the page screenshot
    }

    public String getPasswordLabel() {
        wait.until(ExpectedConditions.visibilityOf(passwordLabel));
        return "Password"; // Based on the page screenshot
    }

    public void clearUsernameField() {
        wait.until(ExpectedConditions.visibilityOf(usernameField));
        usernameField.clear();
        System.out.println("Username field cleared");
    }

    public void clearPasswordField() {
        wait.until(ExpectedConditions.visibilityOf(passwordField));
        passwordField.clear();
        System.out.println("Password field cleared");
    }
}