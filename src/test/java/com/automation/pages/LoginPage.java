package com.automation.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

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
    @FindBy(id = "username")
    private WebElement usernameField;

    @FindBy(id = "password")
    private WebElement passwordField;

    @FindBy(id = "submit")
    private WebElement loginButton;

    @FindBy(id = "error")
    private WebElement errorMessage;

    @FindBy(xpath = "//h1[contains(@class,'post-title')]")
    private WebElement welcomeMessage;

    // Page specific actions

    public void navigateToLoginPage() {
        driver.get("https://practicetestautomation.com/practice-test-login/");
        driver.manage().window().maximize();
        System.out.println("User on login page");
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

    public String getWelcomeMessage() {
        wait.until(ExpectedConditions.visibilityOf(welcomeMessage));
        String message = welcomeMessage.getText();
        System.out.println("Welcome message: " + message);
        return message;
    }

    public boolean isOnDashboard() {
        wait.until(ExpectedConditions.urlContains("logged-in-successfully"));
        return driver.getCurrentUrl().contains("logged-in-successfully");
    }
}