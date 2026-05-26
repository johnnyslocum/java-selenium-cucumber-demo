@ExcelLogin
Feature: Login with Excel Data

  Scenario: Run login tests from Excel sheet
    Given User is on the login page
    And I load users from excel "src/test/resources/testdata/Users.xlsx" sheet "Logins"
    When I run login checks from the excel rows
    Then all excel login checks should pass