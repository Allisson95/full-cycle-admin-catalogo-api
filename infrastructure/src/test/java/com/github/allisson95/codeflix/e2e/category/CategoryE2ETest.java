package com.github.allisson95.codeflix.e2e.category;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
import com.github.allisson95.codeflix.domain.category.CategoryID;
import com.github.allisson95.codeflix.e2e.MockDsl;
import com.github.allisson95.codeflix.infrastructure.category.models.UpdateCategoryRequest;
import com.github.allisson95.codeflix.infrastructure.category.persistence.CategoryRepository;

@E2ETest
@Testcontainers
class CategoryE2ETest implements MockDsl {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private CategoryRepository categoryRepository;

    @Container
    private static final MySQLContainer<?> MYSQL_CONTAINER = new MySQLContainer<>("mysql:8.0")
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
    void asACatalogAdminIShouldBeAbleToCreateANewCategoryWithValidValues() throws Exception {
        assertTrue(MYSQL_CONTAINER.isRunning());
        assertEquals(0, categoryRepository.count());

        final var expectedName = "Filme";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;

        final var categoryId = givenACategory(expectedName, expectedDescription, expectedIsActive);

        final var category = retrieveCategory(categoryId);

        assertEquals(categoryId.getValue(), category.id());
        assertEquals(expectedName, category.name());
        assertEquals(expectedDescription, category.description());
        assertEquals(expectedIsActive, category.active());
        assertNotNull(category.createdAt());
        assertNotNull(category.updatedAt());
        assertNull(category.deletedAt());
    }

    @Test
    void asACatalogAdminIShouldBeAbleToNavigateToAllCategories() throws Exception {
        assertTrue(MYSQL_CONTAINER.isRunning());
        assertEquals(0, categoryRepository.count());

        givenACategory("Filmes", null, true);
        givenACategory("Séries", null, true);
        givenACategory("Documentários", null, true);

        listCategories(0, 1)
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.current_page").value(equalTo(0)))
                .andExpect(jsonPath("$.per_page").value(equalTo(1)))
                .andExpect(jsonPath("$.total").value(equalTo(3)))
                .andExpect(jsonPath("$.items").isArray())
                .andExpect(jsonPath("$.items").value(hasSize(1)))
                .andExpect(jsonPath("$.items[0].name").value(equalTo("Documentários")));

        listCategories(1, 1)
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.current_page").value(equalTo(1)))
                .andExpect(jsonPath("$.per_page").value(equalTo(1)))
                .andExpect(jsonPath("$.total").value(equalTo(3)))
                .andExpect(jsonPath("$.items").isArray())
                .andExpect(jsonPath("$.items").value(hasSize(1)))
                .andExpect(jsonPath("$.items[0].name").value(equalTo("Filmes")));

        listCategories(2, 1)
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.current_page").value(equalTo(2)))
                .andExpect(jsonPath("$.per_page").value(equalTo(1)))
                .andExpect(jsonPath("$.total").value(equalTo(3)))
                .andExpect(jsonPath("$.items").isArray())
                .andExpect(jsonPath("$.items").value(hasSize(1)))
                .andExpect(jsonPath("$.items[0].name").value(equalTo("Séries")));
    }

    @Test
    void asACatalogAdminIShouldBeAbleToSearchBetweenAllCategories() throws Exception {
        assertTrue(MYSQL_CONTAINER.isRunning());
        assertEquals(0, categoryRepository.count());

        givenACategory("Filmes", null, true);
        givenACategory("Séries", null, true);
        givenACategory("Documentários", null, true);

        listCategories(0, 3, "fil")
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.current_page").value(equalTo(0)))
                .andExpect(jsonPath("$.per_page").value(equalTo(3)))
                .andExpect(jsonPath("$.total").value(equalTo(1)))
                .andExpect(jsonPath("$.items").isArray())
                .andExpect(jsonPath("$.items").value(hasSize(1)))
                .andExpect(jsonPath("$.items[0].name").value(equalTo("Filmes")));

        listCategories(0, 3, "doc")
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.current_page").value(equalTo(0)))
                .andExpect(jsonPath("$.per_page").value(equalTo(3)))
                .andExpect(jsonPath("$.total").value(equalTo(1)))
                .andExpect(jsonPath("$.items").isArray())
                .andExpect(jsonPath("$.items").value(hasSize(1)))
                .andExpect(jsonPath("$.items[0].name").value(equalTo("Documentários")));
    }

    @Test
    void asACatalogAdminIShouldBeAbleToSortAllCategoriesByDescriptionDesc() throws Exception {
        assertTrue(MYSQL_CONTAINER.isRunning());
        assertEquals(0, categoryRepository.count());

        givenACategory("Filmes", "F", true);
        givenACategory("Séries", "S", true);
        givenACategory("Documentários", "D", true);

        listCategories(0, 3, "", "description", "desc")
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.current_page").value(equalTo(0)))
                .andExpect(jsonPath("$.per_page").value(equalTo(3)))
                .andExpect(jsonPath("$.total").value(equalTo(3)))
                .andExpect(jsonPath("$.items").isArray())
                .andExpect(jsonPath("$.items").value(hasSize(3)))
                .andExpect(jsonPath("$.items[0].name").value(equalTo("Séries")))
                .andExpect(jsonPath("$.items[1].name").value(equalTo("Filmes")))
                .andExpect(jsonPath("$.items[2].name").value(equalTo("Documentários")));
    }

    @Test
    void asACatalogAdminIShouldBeAbleToGetACategoryByItsIdentifier() throws Exception {
        assertTrue(MYSQL_CONTAINER.isRunning());
        assertEquals(0, categoryRepository.count());

        final var expectedName = "Filme";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;

        final var categoryId = givenACategory(expectedName, expectedDescription, expectedIsActive);

        final var category = retrieveCategory(categoryId);

        assertEquals(categoryId.getValue(), category.id());
        assertEquals(expectedName, category.name());
        assertEquals(expectedDescription, category.description());
        assertEquals(expectedIsActive, category.active());
        assertNotNull(category.createdAt());
        assertNotNull(category.updatedAt());
        assertNull(category.deletedAt());
    }

    @Test
    void asACatalogAdminIShouldBeAbleToSeeATreatedErrorByGettingANotFoundCategory() throws Exception {
        assertTrue(MYSQL_CONTAINER.isRunning());
        assertEquals(0, categoryRepository.count());

        final var aRequest = get("/categories/{categoryId}", 123)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);

        this.mvc.perform(aRequest)
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.message").value(equalTo("Category with id 123 was not found")))
                .andExpect(jsonPath("$.errors").exists())
                .andExpect(jsonPath("$.errors").isArray())
                .andExpect(jsonPath("$.errors").isEmpty());
    }

    @Test
    void asACatalogAdminIShouldBeAbleToUpdateACategoryByItsIdentifier() throws Exception {
        assertTrue(MYSQL_CONTAINER.isRunning());
        assertEquals(0, categoryRepository.count());

        final var categoryId = givenACategory("Movies", null, true);

        final var expectedName = "Filme";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;

        final var aRequestBody = new UpdateCategoryRequest(expectedName, expectedDescription, expectedIsActive);

        updateCategory(categoryId, aRequestBody)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(equalTo(categoryId.getValue())));

        final var categoryJpaEntity = this.categoryRepository.findById(categoryId.getValue()).get();

        assertEquals(categoryId.getValue(), categoryJpaEntity.getId());
        assertEquals(expectedName, categoryJpaEntity.getName());
        assertEquals(expectedDescription, categoryJpaEntity.getDescription());
        assertEquals(expectedIsActive, categoryJpaEntity.isActive());
        assertNotNull(categoryJpaEntity.getCreatedAt());
        assertNotNull(categoryJpaEntity.getUpdatedAt());
        assertNull(categoryJpaEntity.getDeletedAt());
    }

    @Test
    void asACatalogAdminIShouldBeAbleToInactivateACategoryByItsIdentifier() throws Exception {
        assertTrue(MYSQL_CONTAINER.isRunning());
        assertEquals(0, categoryRepository.count());

        final var expectedName = "Filme";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = false;

        final var categoryId = givenACategory(expectedName, expectedDescription, true);

        final var aRequestBody = new UpdateCategoryRequest(expectedName, expectedDescription, expectedIsActive);

        updateCategory(categoryId, aRequestBody)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(equalTo(categoryId.getValue())));

        final var categoryJpaEntity = this.categoryRepository.findById(categoryId.getValue()).get();

        assertEquals(categoryId.getValue(), categoryJpaEntity.getId());
        assertEquals(expectedName, categoryJpaEntity.getName());
        assertEquals(expectedDescription, categoryJpaEntity.getDescription());
        assertEquals(expectedIsActive, categoryJpaEntity.isActive());
        assertNotNull(categoryJpaEntity.getCreatedAt());
        assertNotNull(categoryJpaEntity.getUpdatedAt());
        assertNotNull(categoryJpaEntity.getDeletedAt());
    }

    @Test
    void asACatalogAdminIShouldBeAbleToActivateACategoryByItsIdentifier() throws Exception {
        assertTrue(MYSQL_CONTAINER.isRunning());
        assertEquals(0, categoryRepository.count());

        final var expectedName = "Filme";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;

        final var categoryId = givenACategory(expectedName, expectedDescription, false);

        final var aRequestBody = new UpdateCategoryRequest(expectedName, expectedDescription, expectedIsActive);

        updateCategory(categoryId, aRequestBody)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(equalTo(categoryId.getValue())));

        final var categoryJpaEntity = this.categoryRepository.findById(categoryId.getValue()).get();

        assertEquals(categoryId.getValue(), categoryJpaEntity.getId());
        assertEquals(expectedName, categoryJpaEntity.getName());
        assertEquals(expectedDescription, categoryJpaEntity.getDescription());
        assertEquals(expectedIsActive, categoryJpaEntity.isActive());
        assertNotNull(categoryJpaEntity.getCreatedAt());
        assertNotNull(categoryJpaEntity.getUpdatedAt());
        assertNull(categoryJpaEntity.getDeletedAt());
    }

    @Test
    void asACatalogAdminIShouldBeAbleToDeleteACategoryByItsIdentifier() throws Exception {
        assertTrue(MYSQL_CONTAINER.isRunning());
        assertEquals(0, categoryRepository.count());

        final var categoryId = givenACategory("Filme", "A categoria mais assistida", true);

        deleteACategory(categoryId)
                .andExpect(status().isNoContent());

        assertFalse(this.categoryRepository.existsById(categoryId.getValue()));
    }

    @Test
    void asACatalogAdminIShouldNotSeeAnErrorByDeletingANotExistentCategory() throws Exception {
        assertTrue(MYSQL_CONTAINER.isRunning());
        assertEquals(0, categoryRepository.count());

        deleteACategory(CategoryID.from("123"))
                .andExpect(status().isNoContent());

        assertEquals(0, categoryRepository.count());
    }

}
