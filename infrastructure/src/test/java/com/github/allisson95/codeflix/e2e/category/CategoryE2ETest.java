package com.github.allisson95.codeflix.e2e.category;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.github.allisson95.codeflix.E2ETest;

@E2ETest
@Testcontainers
class CategoryE2ETest {

    @Container
    private static final MySQLContainer<?> MYSQL_CONTAINER = new MySQLContainer<>("mysql:latest")
            .withDatabaseName("admin_catalogo");

    @DynamicPropertySource
    static void setDatasourceProperties(final DynamicPropertyRegistry registry) {
        registry.add("mysql.host", MYSQL_CONTAINER::getHost);
        registry.add("mysql.port", () -> MYSQL_CONTAINER.getMappedPort(MySQLContainer.MYSQL_PORT));
        registry.add("mysql.username", MYSQL_CONTAINER::getUsername);
        registry.add("mysql.password", MYSQL_CONTAINER::getPassword);
    }

    @Test
    void testWorks() {
        assertTrue(MYSQL_CONTAINER.isRunning());
    }

}
