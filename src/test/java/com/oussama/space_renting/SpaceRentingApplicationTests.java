package com.oussama.space_renting;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.MapPropertySource;
import org.springframework.test.context.ActiveProfiles;

import org.testcontainers.containers.Network;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import javax.sql.DataSource;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.fail;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.assertj.core.api.Assertions.assertThat;

/*
 * Setup test context
 */
@SpringBootTest(webEnvironment = RANDOM_PORT)
@ActiveProfiles("test")
@Transactional
public class SpaceRentingApplicationTests {
	@Autowired
	DataSource dataSource;

	@DisplayName("Loading the context")
	@Test
	void contextLoads() {
		assertNotNull(dataSource);
	}

	@DisplayName("Testing db connection")
	@Test
	void testDbConnection() {
		try (Connection connection = dataSource.getConnection()) {
			assertNotNull(connection);
			assertTrue(connection.isValid(2));
			System.out.println("✅ Connection established!");
		} catch ( SQLException e) {
			System.err.println("❌ SQL Exception during connection checking:");
			System.err.println("   Error Code: " + e.getErrorCode());
			System.err.println("   SQL State: " + e.getSQLState());
			System.err.println("   Message: " + e.getMessage());

			fail("Database query execution failed: " + e.getMessage(), e);
		} catch ( Exception e) {
			System.err.println("❌ Unexpected Exception during connection checking:");
			System.err.println("   Exception Type: " + e.getClass().getSimpleName());
			System.err.println("   Message: " + e.getMessage());

			fail("Database query execution failed due to unexpected exception: " + e.getMessage(), e);
		}
	}
}
