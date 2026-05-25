package com.automation.uistepdefinitions;

import com.automation.utilities.DbUtils;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

// Step definitions for database-related steps, using DbUtils for database interactions.
public class DatabaseSteps {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseSteps.class);
    private List<Map<String, Object>> lastQueryResult;
    private int lastUpdateCount;

    // ===== GIVEN steps (setup/precondition) =====

    @Given("the database is accessible")
    public void databaseIsAccessible() {
        try {
            Object result = DbUtils.queryForObject("SELECT 1");
            assertNotNull(result, "Database should be accessible");
            logger.info("[DatabaseSteps] Database is accessible");
        } catch (SQLException e) {
            fail("Database is not accessible: " + e.getMessage());
        }
    }

    @Given("a user with username {string} exists in the database")
    public void userExistsInDatabase(String username) {
        try {
            String sql = "SELECT id FROM users WHERE username = ?";
            Object result = DbUtils.queryForObject(sql, username);
            assertNotNull(result, "User with username '" + username + "' should exist");
            logger.info("[DatabaseSteps] User '{}' found in database (id={})", username, result);
        } catch (SQLException e) {
            fail("Error querying database: " + e.getMessage());
        }
    }

    @Given("a user with username {string} does not exist in the database")
    public void userDoesNotExistInDatabase(String username) {
        try {
            String sql = "SELECT id FROM users WHERE username = ?";
            Object result = DbUtils.queryForObject(sql, username);
            assertNull(result, "User with username '" + username + "' should not exist");
            logger.info("[DatabaseSteps] Confirmed: user '{}' does not exist", username);
        } catch (SQLException e) {
            fail("Error querying database: " + e.getMessage());
        }
    }

    @Given("the user {string} has email {string} in the database")
    public void userHasEmailInDatabase(String username, String expectedEmail) {
        try {
            String sql = "SELECT email FROM users WHERE username = ?";
            Object result = DbUtils.queryForObject(sql, username);
            assertNotNull(result, "User '" + username + "' should exist");
            assertEquals(expectedEmail, result.toString(), "Email should match");
            logger.info("[DatabaseSteps] User '{}' has email '{}'", username, expectedEmail);
        } catch (SQLException e) {
            fail("Error querying database: " + e.getMessage());
        }
    }

    // ===== WHEN steps (actions) =====

    @When("I query the database for all users")
    public void queryAllUsers() {
        try {
            String sql = "SELECT id, username, email FROM users";
            lastQueryResult = DbUtils.executeQuery(sql);
            logger.info("[DatabaseSteps] Query returned {} rows", lastQueryResult.size());
        } catch (SQLException e) {
            fail("Error querying database: " + e.getMessage());
        }
    }

    @When("I count all users in the database")
    public void countAllUsers() {
        try {
            String sql = "SELECT COUNT(*) FROM users";
            Object count = DbUtils.queryForObject(sql);
            assert count != null;
            lastQueryResult = List.of(Map.of("count", count));
            logger.info("[DatabaseSteps] Total users in database: {}", count);
        } catch (SQLException e) {
            fail("Error counting users: " + e.getMessage());
        }
    }

    @When("I query the database for user {string}")
    public void queryUserByUsername(String username) {
        try {
            String sql = "SELECT id, username, email FROM users WHERE username = ?";
            lastQueryResult = DbUtils.executeQuery(sql, username);
            logger.info("[DatabaseSteps] Query for user '{}' returned {} rows", username, lastQueryResult.size());
        } catch (SQLException e) {
            fail("Error querying database: " + e.getMessage());
        }
    }

    @When("I insert a new user with username {string} and email {string}")
    public void insertUser(String username, String email) {
        try {
            String sql = "INSERT INTO users (username, email) VALUES (?, ?)";
            lastUpdateCount = DbUtils.executeUpdate(sql, username, email);
            logger.info("[DatabaseSteps] Inserted user '{}' with email '{}' ({} rows affected)",
                    username, email, lastUpdateCount);
        } catch (SQLException e) {
            fail("Error inserting user: " + e.getMessage());
        }
    }

    @When("I update user {string} with email {string}")
    public void updateUserEmail(String username, String newEmail) {
        try {
            String sql = "UPDATE users SET email = ? WHERE username = ?";
            lastUpdateCount = DbUtils.executeUpdate(sql, newEmail, username);
            logger.info("[DatabaseSteps] Updated user '{}' with email '{}' ({} rows affected)",
                    username, newEmail, lastUpdateCount);
        } catch (SQLException e) {
            fail("Error updating user: " + e.getMessage());
        }
    }

    @When("I delete user {string} from the database")
    public void deleteUser(String username) {
        try {
            String sql = "DELETE FROM users WHERE username = ?";
            lastUpdateCount = DbUtils.executeUpdate(sql, username);
            logger.info("[DatabaseSteps] Deleted user '{}' ({} rows affected)", username, lastUpdateCount);
        } catch (SQLException e) {
            fail("Error deleting user: " + e.getMessage());
        }
    }

    // ===== THEN steps (validation/assertions) =====

    @Then("the query should return {int} row(s)")
    public void queryReturnsRows(int expectedCount) {
        assertNotNull(lastQueryResult, "Query result should not be null");
        assertEquals(expectedCount, lastQueryResult.size(), "Query should return " + expectedCount + " row(s)");
        logger.info("[DatabaseSteps] Query returned expected {} row(s)", expectedCount);
    }

    @Then("the query should return no rows")
    public void queryReturnsNoRows() {
        assertNotNull(lastQueryResult, "Query result should not be null");
        assertEquals(0, lastQueryResult.size(), "Query should return 0 rows");
        logger.info("[DatabaseSteps] Query returned 0 rows as expected");
    }

    @Then("the last update should affect {int} row")
    public void lastUpdateAffectsRow(int expectedCount) {
        assertEquals(expectedCount, lastUpdateCount, "Last update should affect " + expectedCount + " row(s)");
        logger.info("[DatabaseSteps] Last update affected {} row(s) as expected", expectedCount);
    }

    @Then("the result should contain a user with username {string}")
    public void resultContainsUsername(String expectedUsername) {
        assertNotNull(lastQueryResult, "Query result should not be null");
        boolean found = lastQueryResult.stream()
                .anyMatch(row -> row.get("username") != null && row.get("username").toString().equals(expectedUsername));
        assertTrue(found, "Result should contain user with username '" + expectedUsername + "'");
        logger.info("[DatabaseSteps] Found user '{}' in result", expectedUsername);
    }

    @Then("the result should contain email {string}")
    public void resultContainsEmail(String expectedEmail) {
        assertNotNull(lastQueryResult, "Query result should not be null");
        boolean found = lastQueryResult.stream()
                .anyMatch(row -> row.get("email") != null && row.get("email").toString().equals(expectedEmail));
        assertTrue(found, "Result should contain email '" + expectedEmail + "'");
        logger.info("[DatabaseSteps] Found email '{}' in result", expectedEmail);
    }

    @Then("the result should not contain a user with username {string}")
    public void resultDoesNotContainUsername(String username) {
        assertNotNull(lastQueryResult, "Query result should not be null");
        boolean found = lastQueryResult.stream()
                .anyMatch(row -> row.get("username") != null && row.get("username").toString().equals(username));
        assertFalse(found, "Result should not contain user with username '" + username + "'");
        logger.info("[DatabaseSteps] Confirmed: user '{}' not found in result", username);
    }
}