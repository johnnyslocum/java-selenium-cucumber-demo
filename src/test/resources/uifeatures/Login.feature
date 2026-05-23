@LoginTest
Feature: Login

  Background:
    Given User is on the login page

  Scenario: Verify login page elements are displayed
    Then the username input field should be visible
    And the password input field should be visible
    And the login button should be visible
    And the login page heading should display "Test login"

  Scenario: Verify login page description text
    Then the page should display login instruction text
    And the page should show correct credentials "student" and "Password123"

  Scenario: Verify login form labels
    Then the username label should display "Username"
    And the password label should display "Password"

  Scenario: Successful login with valid credentials
    When User enters username "student"
    And User enters password "Password123"
    And User clicks on login button
    Then User should be redirected to dashboard
    And User should see welcome message

  Scenario: Login with invalid credentials
    When User enters username "invalidUser"
    And User enters password "wrongPassword"
    And User clicks on login button
    Then User should see error message

  Scenario Outline: Login with multiple users (Data-Driven Testing)
    When User enters username "<username>"
    And User enters password "<password>"
    And User clicks on login button
    Then User should see "<result>"
    Examples:
      | username    | password    | result        |
      | student     | Password123 | Dashboard     |
      | student     | wrongPass   | Error message |
      | invalidUser | wrongPass   | Error message |
      | wrongUser   | Password123 | Error message |
#      | student     | Password123 | Error message |
    # Note: The last row is to show a failing test case result.

  Scenario: Submit login form with empty fields
    When User clicks on login button without entering credentials
    Then User should see appropriate error or validation message

  Scenario: Enter only username and submit
    When User enters username "student"
    And User clicks on login button without password
    Then User should see error message

  Scenario: Enter only password and submit
    When User enters password "Password123"
    And User clicks on login button without username
    Then User should see error message

  Scenario: Verify error message text for invalid login
    When User enters username "test"
    And User enters password "test"
    And User clicks on login button
    Then the error message should contain "Your username is invalid!"

  Scenario: Verify case sensitivity in username
    When User enters username "STUDENT"
    And User enters password "Password123"
    And User clicks on login button
    Then User should see error message

  Scenario: Verify case sensitivity in password
    When User enters username "student"
    And User enters password "password123"
    And User clicks on login button
    Then User should see error message

  Scenario: Clear username field and re-enter
    When User enters username "invalidUser"
    And User clears username field
    And User enters username "student"
    And User enters password "Password123"
    And User clicks on login button
    Then User should be redirected to dashboard

  Scenario: Clear password field and re-enter
    When User enters username "student"
    And User enters password "wrongPassword"
    And User clears password field
    And User enters password "Password123"
    And User clicks on login button
    Then User should be redirected to dashboard