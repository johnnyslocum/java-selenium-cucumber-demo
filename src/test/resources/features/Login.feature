@LoginTest
Feature: Login

  Background:
    Given User is on the login page

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
      | username     | password     | result              |
      | student      | Password123  | Dashboard           |
      | student      | wrongPass    | Error message       |
      | invalidUser  | wrongPass    | Error message       |
      | wrongUser    | Password123  | Error message       |