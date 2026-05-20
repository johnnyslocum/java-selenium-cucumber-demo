@ApiTest

Feature: Karate Demo

  Scenario: Retrieve user by ID
    Given url 'https://jsonplaceholder.typicode.com/users/1'
    When method GET
    Then status 200
    And match $.id == 1
