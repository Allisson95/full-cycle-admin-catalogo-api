package com.github.allisson95.codeflix.e2e.castmember;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.github.allisson95.codeflix.E2ETest;
import com.github.allisson95.codeflix.domain.Fixture;
import com.github.allisson95.codeflix.domain.castmember.CastMemberID;
import com.github.allisson95.codeflix.domain.castmember.CastMemberType;
import com.github.allisson95.codeflix.e2e.MockDsl;
import com.github.allisson95.codeflix.infrastructure.ApiTest;
import com.github.allisson95.codeflix.infrastructure.castmember.models.UpdateCastMemberRequest;
import com.github.allisson95.codeflix.infrastructure.castmember.persistence.CastMemberRepository;

@E2ETest
@Testcontainers
class CastMemberE2ETest implements MockDsl {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private CastMemberRepository castMemberRepository;

    @Container
    private static final MySQLContainer<?> MYSQL_CONTAINER = new MySQLContainer<>("mysql:8.0")
            .withDatabaseName("admin_catalogo");

    @DynamicPropertySource
    public static void setDatasourceProperties(final DynamicPropertyRegistry registry) {
        registry.add("mysql.url", MYSQL_CONTAINER::getJdbcUrl);
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
        final var expectedType = Fixture.CastMembers.type();

        final var actualMemberId = givenACastMember(expectedName, expectedType);

        final var actualMember = retrieveCastMember(actualMemberId);

        assertEquals(expectedName, actualMember.name());
        assertEquals(expectedType, actualMember.type());
        assertNotNull(actualMember.createdAt());
        assertNotNull(actualMember.updatedAt());
        assertEquals(actualMember.createdAt(), actualMember.updatedAt());
    }

    @Test
    void asACatalogAdminIShouldBeAbleToNavigateToAllCastMembers() throws Exception {
        assertTrue(MYSQL_CONTAINER.isRunning());
        assertEquals(0, this.castMemberRepository.count());

        givenACastMember("Vin Diesel", CastMemberType.ACTOR);
        givenACastMember("Steven Spielberg", CastMemberType.DIRECTOR);
        givenACastMember("Nicolas Cage", CastMemberType.ACTOR);

        listCastMembers(0, 1)
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.current_page").value(equalTo(0)))
                .andExpect(jsonPath("$.per_page").value(equalTo(1)))
                .andExpect(jsonPath("$.total").value(equalTo(3)))
                .andExpect(jsonPath("$.items").isArray())
                .andExpect(jsonPath("$.items").value(hasSize(1)))
                .andExpect(jsonPath("$.items[0].name").value(equalTo("Nicolas Cage")));

        listCastMembers(1, 1)
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.current_page").value(equalTo(1)))
                .andExpect(jsonPath("$.per_page").value(equalTo(1)))
                .andExpect(jsonPath("$.total").value(equalTo(3)))
                .andExpect(jsonPath("$.items").isArray())
                .andExpect(jsonPath("$.items").value(hasSize(1)))
                .andExpect(jsonPath("$.items[0].name").value(equalTo("Steven Spielberg")));

        listCastMembers(2, 1)
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.current_page").value(equalTo(2)))
                .andExpect(jsonPath("$.per_page").value(equalTo(1)))
                .andExpect(jsonPath("$.total").value(equalTo(3)))
                .andExpect(jsonPath("$.items").isArray())
                .andExpect(jsonPath("$.items").value(hasSize(1)))
                .andExpect(jsonPath("$.items[0].name").value(equalTo("Vin Diesel")));
    }

    @Test
    void asACatalogAdminIShouldBeAbleToSearchBetweenAllCastMembers() throws Exception {
        assertTrue(MYSQL_CONTAINER.isRunning());
        assertEquals(0, this.castMemberRepository.count());

        givenACastMember("Vin Diesel", CastMemberType.ACTOR);
        givenACastMember("Steven Spielberg", CastMemberType.DIRECTOR);
        givenACastMember("Nicolas Cage", CastMemberType.ACTOR);

        listCastMembers(0, 3, "rg")
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.current_page").value(equalTo(0)))
                .andExpect(jsonPath("$.per_page").value(equalTo(3)))
                .andExpect(jsonPath("$.total").value(equalTo(1)))
                .andExpect(jsonPath("$.items").isArray())
                .andExpect(jsonPath("$.items").value(hasSize(1)))
                .andExpect(jsonPath("$.items[0].name").value(equalTo("Steven Spielberg")));

        listCastMembers(0, 3, "di")
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.current_page").value(equalTo(0)))
                .andExpect(jsonPath("$.per_page").value(equalTo(3)))
                .andExpect(jsonPath("$.total").value(equalTo(1)))
                .andExpect(jsonPath("$.items").isArray())
                .andExpect(jsonPath("$.items").value(hasSize(1)))
                .andExpect(jsonPath("$.items[0].name").value(equalTo("Vin Diesel")));

        listCastMembers(0, 3, "ge")
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.current_page").value(equalTo(0)))
                .andExpect(jsonPath("$.per_page").value(equalTo(3)))
                .andExpect(jsonPath("$.total").value(equalTo(1)))
                .andExpect(jsonPath("$.items").isArray())
                .andExpect(jsonPath("$.items").value(hasSize(1)))
                .andExpect(jsonPath("$.items[0].name").value(equalTo("Nicolas Cage")));
    }

    @Test
    void asACatalogAdminIShouldBeAbleToSortAllCastMembersByNameDesc() throws Exception {
        assertTrue(MYSQL_CONTAINER.isRunning());
        assertEquals(0, this.castMemberRepository.count());

        givenACastMember("Nicolas Cage", CastMemberType.ACTOR);
        givenACastMember("Vin Diesel", CastMemberType.ACTOR);
        givenACastMember("Steven Spielberg", CastMemberType.DIRECTOR);

        listCastMembers(0, 3, "", "name", "desc")
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.current_page").value(equalTo(0)))
                .andExpect(jsonPath("$.per_page").value(equalTo(3)))
                .andExpect(jsonPath("$.total").value(equalTo(3)))
                .andExpect(jsonPath("$.items").isArray())
                .andExpect(jsonPath("$.items").value(hasSize(3)))
                .andExpect(jsonPath("$.items[0].name").value(equalTo("Vin Diesel")))
                .andExpect(jsonPath("$.items[1].name").value(equalTo("Steven Spielberg")))
                .andExpect(jsonPath("$.items[2].name").value(equalTo("Nicolas Cage")));
    }

    @Test
    void asACatalogAdminIShouldBeAbleToGetACastMemberByItsIdentifier() throws Exception {
        assertTrue(MYSQL_CONTAINER.isRunning());
        assertEquals(0, this.castMemberRepository.count());

        final var expectedName = Fixture.name();
        final var expectedType = Fixture.CastMembers.type();

        final var actualMemberId = givenACastMember(expectedName, expectedType);

        final var actualMember = retrieveCastMember(actualMemberId);

        assertEquals(expectedName, actualMember.name());
        assertEquals(expectedType, actualMember.type());
        assertNotNull(actualMember.createdAt());
        assertNotNull(actualMember.updatedAt());
        assertEquals(actualMember.createdAt(), actualMember.updatedAt());
    }

    @Test
    void asACatalogAdminIShouldBeAbleToSeeATreatedErrorByGettingANotFoundCastMember() throws Exception {
        assertTrue(MYSQL_CONTAINER.isRunning());
        assertEquals(0, this.castMemberRepository.count());

        final var aRequest = get("/cast_members/{castMemberId}", 123)
                .with(ApiTest.ADMIN_JWT)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);

        this.mvc.perform(aRequest)
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.message").value(equalTo("CastMember with id 123 was not found")))
                .andExpect(jsonPath("$.errors").exists())
                .andExpect(jsonPath("$.errors").isArray())
                .andExpect(jsonPath("$.errors").isEmpty());
    }

    @Test
    void asACatalogAdminIShouldBeAbleToUpdateACastMemberByItsIdentifier() throws Exception {
        assertTrue(MYSQL_CONTAINER.isRunning());
        assertEquals(0, this.castMemberRepository.count());

        final var castMemberId = givenACastMember("Vi Diesel", CastMemberType.DIRECTOR);

        final var expectedName = "Vin Diesel";
        final var expectedType = CastMemberType.ACTOR;

        final var aRequestBody = new UpdateCastMemberRequest(expectedName, expectedType);

        updateCastMember(castMemberId, aRequestBody)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(equalTo(castMemberId.getValue())));

        final var castMemberJpaEntity = this.castMemberRepository.findById(castMemberId.getValue()).get();

        assertEquals(castMemberId.getValue(), castMemberJpaEntity.getId());
        assertEquals(expectedName, castMemberJpaEntity.getName());
        assertEquals(expectedType, castMemberJpaEntity.getType());
        assertNotNull(castMemberJpaEntity.getCreatedAt());
        assertNotNull(castMemberJpaEntity.getUpdatedAt());
    }

    @Test
    void asACatalogAdminIShouldBeAbleToDeleteACastMemberByItsIdentifier() throws Exception {
        assertTrue(MYSQL_CONTAINER.isRunning());
        assertEquals(0, this.castMemberRepository.count());

        final var castMemberId = givenACastMember(Fixture.name(), Fixture.CastMembers.type());

        deleteACastMember(castMemberId)
                .andExpect(status().isNoContent());

        assertFalse(this.castMemberRepository.existsById(castMemberId.getValue()));
    }

    @Test
    void asACatalogAdminIShouldNotSeeAnErrorByDeletingANotExistentCastMember() throws Exception {
        assertTrue(MYSQL_CONTAINER.isRunning());
        assertEquals(0, this.castMemberRepository.count());

        deleteACastMember(CastMemberID.from("123"))
                .andExpect(status().isNoContent());

        assertEquals(0, this.castMemberRepository.count());
    }

}
