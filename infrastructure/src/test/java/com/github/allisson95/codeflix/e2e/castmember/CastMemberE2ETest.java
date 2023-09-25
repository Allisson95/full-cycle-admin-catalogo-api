package com.github.allisson95.codeflix.e2e.castmember;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.github.allisson95.codeflix.E2ETest;
import com.github.allisson95.codeflix.Fixture;
import com.github.allisson95.codeflix.e2e.MockDsl;
import com.github.allisson95.codeflix.infrastructure.castmember.persistence.CastMemberRepository;

@E2ETest
@Testcontainers
class CastMemberE2ETest implements MockDsl {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private CastMemberRepository castMemberRepository;

    @Container
    private static final MySQLContainer<?> MYSQL_CONTAINER = new MySQLContainer<>("mysql:latest")
            .withDatabaseName("admin_catalogo");

    @DynamicPropertySource
    public static void setDatasourceProperties(final DynamicPropertyRegistry registry) {
        registry.add("mysql.host", MYSQL_CONTAINER::getHost);
        registry.add("mysql.port", () -> MYSQL_CONTAINER.getMappedPort(MySQLContainer.MYSQL_PORT));
        registry.add("mysql.username", MYSQL_CONTAINER::getUsername);
        registry.add("mysql.password", MYSQL_CONTAINER::getPassword);
    }

    @Override
    public MockMvc mvc() {
        return this.mvc;
    }

    @Test
    void asACatalogAdminIShouldBeAbleToCreateANewCastMemberWithValidValues() throws Exception {
        assertTrue(MYSQL_CONTAINER.isRunning());
        assertEquals(0, this.castMemberRepository.count());

        final var expectedName = Fixture.name();
        final var expectedType = Fixture.CastMember.type();

        final var actualMemberId = givenACastMember(expectedName, expectedType);

        final var actualMember = retrieveCastMember(actualMemberId);

        assertEquals(expectedName, actualMember.name());
        assertEquals(expectedType, actualMember.type());
        assertNotNull(actualMember.createdAt());
        assertNotNull(actualMember.updatedAt());
        assertEquals(actualMember.createdAt(), actualMember.updatedAt());
    }

}
