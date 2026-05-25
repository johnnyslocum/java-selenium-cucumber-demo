@Db
Feature: Database Operations

  Background:
    Given the database is accessible

  Scenario: Query all users and get a users count
    When I query the database for all users
    Then I count all users in the database

  Scenario: Query all users and confirm a user not in the results
    When I query the database for all users
    Then the result should not contain a user with username "nonexistentuser"

  Scenario: Query user by username
    Given a user with username "student" exists in the database
    When I query the database for user "student"
    Then the query should return 1 row
    And the result should contain a user with username "student"

  Scenario: Update user email
    Given a user with username "student" exists in the database
    When I update user "student" with email "newemail@example.com"
    Then the last update should affect 1 row
    And the result should contain email "newemail@example.com"

  Scenario: Insert and verify new user
    Given a user with username "testuser99" does not exist in the database
    When I insert a new user with username "testuser99" and email "test99@example.com"
    Then the last update should affect 1 row
    And the user "testuser99" has email "test99@example.com" in the database

  Scenario: Delete and verify user is gone
    Given a user with username "testuser99" exists in the database
    When I delete user "testuser99" from the database
    Then the last update should affect 1 row
    And a user with username "testuser99" does not exist in the database

  Scenario: Query user by username that does not exist
    Given a user with username "nonexistentuser" does not exist in the database
    When I query the database for user "nonexistentuser"
    Then the query should return no rows