package com.midasdev.mybg;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

import lombok.extern.slf4j.Slf4j;

/**
 * Base class for database integration tests.
 * <p>
 * Supports two test profiles:
 * <ul>
 * <li><b>test</b> (default): Uses local MySQL database</li>
 * <li><b>test-ci</b>: Uses Testcontainers for MySQL</li>
 * </ul>
 * <p>
 * To run tests with Testcontainers, activate the 'test-ci' profile:
 * 
 * <pre>
 * ./gradlew test -Dspring.profiles.active=test_ci
 * </pre>
 */
@Slf4j
@Testcontainers
public abstract class DatabaseTestSupport {

    private static final String TESTCONTAINERS_PROFILE = "test_ci";
    protected static MySQLContainer<?> mysql;

    @BeforeAll
    static void setupDatabase() {
        String activeProfile = System.getProperty("spring.profiles.active", "test");

        if (activeProfile.equals(TESTCONTAINERS_PROFILE)) {
            mysql = new MySQLContainer<>("mysql:8.0")
                    .withDatabaseName("testdb")
                    .withUsername("testuser")
                    .withPassword("testpass");
            log.info("Starting MySQL Testcontainer for profile '{}'", TESTCONTAINERS_PROFILE);
            mysql.start();
        }
    }

    @AfterAll
    static void teardownDatabase() {
        if (mysql != null && mysql.isRunning()) {
            mysql.stop();
        }
    }

}
