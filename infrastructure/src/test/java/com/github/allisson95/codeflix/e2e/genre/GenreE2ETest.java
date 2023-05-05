package com.github.allisson95.codeflix.e2e.genre;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

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
import com.github.allisson95.codeflix.domain.category.CategoryID;
import com.github.allisson95.codeflix.domain.genre.GenreID;
import com.github.allisson95.codeflix.e2e.MockDsl;
import com.github.allisson95.codeflix.infrastructure.genre.models.UpdateGenreRequest;
import com.github.allisson95.codeflix.infrastructure.genre.persistence.GenreRepository;

@E2ETest
@Testcontainers
class GenreE2ETest implements MockDsl {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private GenreRepository genreRepository;

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

    @Override
    public MockMvc mvc() {
        return this.mvc;
    }

    @Test
    void asACatalogAdminIShouldBeAbleToCreateANewGenreWithValidValues() throws Exception {
        assertTrue(MYSQL_CONTAINER.isRunning());
        assertEquals(0, genreRepository.count());

        final var expectedName = "Ação";
        final var expectedCategories = List.<CategoryID>of();
        final var expectedIsActive = true;

        final var genreId = givenAGenre(expectedName, expectedCategories, expectedIsActive);

        final var actualGenre = this.genreRepository.findById(genreId.getValue()).get();

        assertEquals(genreId.getValue(), actualGenre.getId());
        assertEquals(expectedName, actualGenre.getName());
        assertThat(actualGenre.getCategoryIDs(), containsInAnyOrder(expectedCategories.toArray()));
        assertEquals(expectedIsActive, actualGenre.isActive());
        assertNotNull(actualGenre.getCreatedAt());
        assertNotNull(actualGenre.getUpdatedAt());
        assertNull(actualGenre.getDeletedAt());
    }

    @Test
    void asACatalogAdminIShouldBeAbleToCreateANewGenreWithValidValuesAndCategories() throws Exception {
        assertTrue(MYSQL_CONTAINER.isRunning());
        assertEquals(0, genreRepository.count());

        final var filmes = givenACategory("Filmes", null, true);
        final var expectedName = "Ação";
        final var expectedCategories = List.<CategoryID>of(filmes);
        final var expectedIsActive = true;

        final var genreId = givenAGenre(expectedName, expectedCategories, expectedIsActive);

        final var actualGenre = this.genreRepository.findById(genreId.getValue()).get();

        assertEquals(genreId.getValue(), actualGenre.getId());
        assertEquals(expectedName, actualGenre.getName());
        assertThat(actualGenre.getCategoryIDs(), containsInAnyOrder(expectedCategories.toArray()));
        assertEquals(expectedIsActive, actualGenre.isActive());
        assertNotNull(actualGenre.getCreatedAt());
        assertNotNull(actualGenre.getUpdatedAt());
        assertNull(actualGenre.getDeletedAt());
    }

    @Test
    void asACatalogAdminIShouldBeAbleToNavigateToAllGenres() throws Exception {
        assertTrue(MYSQL_CONTAINER.isRunning());
        assertEquals(0, genreRepository.count());

        givenAGenre("Ação", List.<CategoryID>of(), true);
        givenAGenre("Drama", List.<CategoryID>of(), true);
        givenAGenre("Esportes", List.<CategoryID>of(), true);

        listGenres(0, 1)
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.current_page").value(equalTo(0)))
                .andExpect(jsonPath("$.per_page").value(equalTo(1)))
                .andExpect(jsonPath("$.total").value(equalTo(3)))
                .andExpect(jsonPath("$.items").isArray())
                .andExpect(jsonPath("$.items").value(hasSize(1)))
                .andExpect(jsonPath("$.items[0].name").value(equalTo("Ação")));

        listGenres(1, 1)
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.current_page").value(equalTo(1)))
                .andExpect(jsonPath("$.per_page").value(equalTo(1)))
                .andExpect(jsonPath("$.total").value(equalTo(3)))
                .andExpect(jsonPath("$.items").isArray())
                .andExpect(jsonPath("$.items").value(hasSize(1)))
                .andExpect(jsonPath("$.items[0].name").value(equalTo("Drama")));

        listGenres(2, 1)
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.current_page").value(equalTo(2)))
                .andExpect(jsonPath("$.per_page").value(equalTo(1)))
                .andExpect(jsonPath("$.total").value(equalTo(3)))
                .andExpect(jsonPath("$.items").isArray())
                .andExpect(jsonPath("$.items").value(hasSize(1)))
                .andExpect(jsonPath("$.items[0].name").value(equalTo("Esportes")));
    }

    @Test
    void asACatalogAdminIShouldBeAbleToSearchBetweenAllGenres() throws Exception {
        assertTrue(MYSQL_CONTAINER.isRunning());
        assertEquals(0, genreRepository.count());

        givenAGenre("Ação", List.<CategoryID>of(), true);
        givenAGenre("Drama", List.<CategoryID>of(), true);
        givenAGenre("Esportes", List.<CategoryID>of(), true);

        listGenres(0, 3, "dra")
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.current_page").value(equalTo(0)))
                .andExpect(jsonPath("$.per_page").value(equalTo(3)))
                .andExpect(jsonPath("$.total").value(equalTo(1)))
                .andExpect(jsonPath("$.items").isArray())
                .andExpect(jsonPath("$.items").value(hasSize(1)))
                .andExpect(jsonPath("$.items[0].name").value(equalTo("Drama")));

        listGenres(0, 3, "es")
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.current_page").value(equalTo(0)))
                .andExpect(jsonPath("$.per_page").value(equalTo(3)))
                .andExpect(jsonPath("$.total").value(equalTo(1)))
                .andExpect(jsonPath("$.items").isArray())
                .andExpect(jsonPath("$.items").value(hasSize(1)))
                .andExpect(jsonPath("$.items[0].name").value(equalTo("Esportes")));
    }

    @Test
    void asACatalogAdminIShouldBeAbleToSortAllGenresByNameDesc() throws Exception {
        assertTrue(MYSQL_CONTAINER.isRunning());
        assertEquals(0, genreRepository.count());

        givenAGenre("Ação", List.<CategoryID>of(), true);
        givenAGenre("Drama", List.<CategoryID>of(), true);
        givenAGenre("Esportes", List.<CategoryID>of(), true);

        listGenres(0, 3, "", "name", "desc")
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.current_page").value(equalTo(0)))
                .andExpect(jsonPath("$.per_page").value(equalTo(3)))
                .andExpect(jsonPath("$.total").value(equalTo(3)))
                .andExpect(jsonPath("$.items").isArray())
                .andExpect(jsonPath("$.items").value(hasSize(3)))
                .andExpect(jsonPath("$.items[0].name").value(equalTo("Esportes")))
                .andExpect(jsonPath("$.items[1].name").value(equalTo("Drama")))
                .andExpect(jsonPath("$.items[2].name").value(equalTo("Ação")));
    }

    @Test
    void asACatalogAdminIShouldBeAbleToGetAGenreByItsIdentifier() throws Exception {
        assertTrue(MYSQL_CONTAINER.isRunning());
        assertEquals(0, genreRepository.count());

        final var filmes = givenACategory("Filmes", null, true);
        final var expectedName = "Ação";
        final var expectedCategories = List.<CategoryID>of(filmes);
        final var expectedIsActive = true;

        final var genreId = givenAGenre(expectedName, expectedCategories, expectedIsActive);

        final var genre = retrieveGenre(genreId);

        assertEquals(genreId.getValue(), genre.id());
        assertEquals(expectedName, genre.name());
        assertThat(genre.categories(), containsInAnyOrder(mapTo(expectedCategories, CategoryID::getValue).toArray()));
        assertEquals(expectedIsActive, genre.active());
        assertNotNull(genre.createdAt());
        assertNotNull(genre.updatedAt());
        assertNull(genre.deletedAt());
    }

    @Test
    void asACatalogAdminIShouldBeAbleToSeeATreatedErrorByGettingANotFoundGenre() throws Exception {
        assertTrue(MYSQL_CONTAINER.isRunning());
        assertEquals(0, genreRepository.count());

        final var aRequest = get("/genres/{genreID}", 123)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);

        this.mvc.perform(aRequest)
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.message").value(equalTo("Genre with id 123 was not found")))
                .andExpect(jsonPath("$.errors").exists())
                .andExpect(jsonPath("$.errors").isArray())
                .andExpect(jsonPath("$.errors").isEmpty());
    }

    @Test
    void asACatalogAdminIShouldBeAbleToUpdateAGenreByItsIdentifier() throws Exception {
        assertTrue(MYSQL_CONTAINER.isRunning());
        assertEquals(0, genreRepository.count());

        final var filmes = givenACategory("Filmes", null, true);

        final var expectedName = "Ação";
        final var expectedCategories = List.<CategoryID>of(filmes);
        final var expectedIsActive = true;

        final var genreId = givenAGenre("acao", expectedCategories, expectedIsActive);

        final var aRequestBody = new UpdateGenreRequest(expectedName, mapTo(expectedCategories, CategoryID::getValue), expectedIsActive);

        updateGenre(genreId, aRequestBody)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(equalTo(genreId.getValue())));

        final var genreJpaEntity = this.genreRepository.findById(genreId.getValue()).get();

        assertEquals(genreId.getValue(), genreJpaEntity.getId());
        assertEquals(expectedName, genreJpaEntity.getName());
        assertThat(genreJpaEntity.getCategoryIDs(), containsInAnyOrder(expectedCategories.toArray(new CategoryID[expectedCategories.size()])));
        assertEquals(expectedIsActive, genreJpaEntity.isActive());
        assertNotNull(genreJpaEntity.getCreatedAt());
        assertNotNull(genreJpaEntity.getUpdatedAt());
        assertNull(genreJpaEntity.getDeletedAt());
    }

    @Test
    void asACatalogAdminIShouldBeAbleToInactivateAGenreByItsIdentifier() throws Exception {
        assertTrue(MYSQL_CONTAINER.isRunning());
        assertEquals(0, genreRepository.count());

        final var expectedName = "Ação";
        final var expectedCategories = List.<CategoryID>of();
        final var expectedIsActive = false;

        final var genreId = givenAGenre(expectedName, expectedCategories, true);

        final var aRequestBody = new UpdateGenreRequest(expectedName, mapTo(expectedCategories, CategoryID::getValue), expectedIsActive);

        updateGenre(genreId, aRequestBody)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(equalTo(genreId.getValue())));

        final var genreJpaEntity = this.genreRepository.findById(genreId.getValue()).get();

        assertEquals(genreId.getValue(), genreJpaEntity.getId());
        assertEquals(expectedName, genreJpaEntity.getName());
        assertThat(genreJpaEntity.getCategoryIDs(), containsInAnyOrder(expectedCategories.toArray(new CategoryID[expectedCategories.size()])));
        assertEquals(expectedIsActive, genreJpaEntity.isActive());
        assertNotNull(genreJpaEntity.getCreatedAt());
        assertNotNull(genreJpaEntity.getUpdatedAt());
        assertNotNull(genreJpaEntity.getDeletedAt());
    }

    @Test
    void asACatalogAdminIShouldBeAbleToActivateAGenreByItsIdentifier() throws Exception {
        assertTrue(MYSQL_CONTAINER.isRunning());
        assertEquals(0, genreRepository.count());

        final var expectedName = "Ação";
        final var expectedCategories = List.<CategoryID>of();
        final var expectedIsActive = true;

        final var genreId = givenAGenre(expectedName, expectedCategories, false);

        final var aRequestBody = new UpdateGenreRequest(expectedName, mapTo(expectedCategories, CategoryID::getValue), expectedIsActive);

        updateGenre(genreId, aRequestBody)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(equalTo(genreId.getValue())));

        final var genreJpaEntity = this.genreRepository.findById(genreId.getValue()).get();

        assertEquals(genreId.getValue(), genreJpaEntity.getId());
        assertEquals(expectedName, genreJpaEntity.getName());
        assertThat(genreJpaEntity.getCategoryIDs(), containsInAnyOrder(expectedCategories.toArray(new CategoryID[expectedCategories.size()])));
        assertEquals(expectedIsActive, genreJpaEntity.isActive());
        assertNotNull(genreJpaEntity.getCreatedAt());
        assertNotNull(genreJpaEntity.getUpdatedAt());
        assertNull(genreJpaEntity.getDeletedAt());
    }

    @Test
    void asACatalogAdminIShouldBeAbleToDeleteAGenreByItsIdentifier() throws Exception {
        assertTrue(MYSQL_CONTAINER.isRunning());
        assertEquals(0, genreRepository.count());

        final var genreId = givenAGenre("Ação", List.<CategoryID>of(), true);

        deleteAGenre(genreId)
                .andExpect(status().isNoContent());

        assertFalse(this.genreRepository.existsById(genreId.getValue()));
        assertEquals(0, genreRepository.count());
    }

    @Test
    void asACatalogAdminIShouldNotSeeAnErrorByDeletingANotExistentGenre() throws Exception {
        assertTrue(MYSQL_CONTAINER.isRunning());
        assertEquals(0, genreRepository.count());

        deleteAGenre(GenreID.from("123"))
                .andExpect(status().isNoContent());

        assertEquals(0, genreRepository.count());
    }

}
