package com.midasdev.mybg;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;

import lombok.extern.slf4j.Slf4j;

/**
 * Base class for database integration tests.
 * <p>
 * Supports two test profiles:
 * <ul>
 * <li><b>test</b> (default): Uses local MySQL database</li>
 * <li><b>test-containers</b>: Uses Testcontainers for MySQL</li>
 * </ul>
 * <p>
 * To run tests with Testcontainers, activate the 'test-containers' profile:
 * 
 * <pre>
 * ./gradlew test -Dspring.profiles.active=test_local
 * </pre>
 */
@Slf4j
public abstract class DatabaseTestSupport {

    private static final String TESTCONTAINERS_PROFILE = "test_ci";
    private static MySQLContainer<?> mysql;

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

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        String activeProfile = System.getProperty("spring.profiles.active", "test");

        // Only override datasource properties when using Testcontainers
        if (activeProfile.equals(TESTCONTAINERS_PROFILE) && mysql != null) {
            registry.add("spring.datasource.url", mysql::getJdbcUrl);
            registry.add("spring.datasource.username", mysql::getUsername);
            registry.add("spring.datasource.password", mysql::getPassword);
        }
    }

    @AfterAll
    static void teardownDatabase() {
        if (mysql != null && mysql.isRunning()) {
            mysql.stop();
        }
    }

}
