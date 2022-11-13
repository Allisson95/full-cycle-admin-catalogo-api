package com.github.allisson95.codeflix.e2e.category;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
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
import com.github.allisson95.codeflix.infrastructure.category.models.CategoryResponse;
import com.github.allisson95.codeflix.infrastructure.category.models.CreateCategoryRequest;
import com.github.allisson95.codeflix.infrastructure.category.persistence.CategoryRepository;
import com.github.allisson95.codeflix.infrastructure.configuration.json.Json;

@E2ETest
@Testcontainers
class CategoryE2ETest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private CategoryRepository categoryRepository;

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

    private CategoryID givenACategory(final String name, final String description, final boolean isActive)
            throws Exception {
        final var aRequestBody = new CreateCategoryRequest(name, description, isActive);

        final var aRequest = post("/categories")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(Json.writeValueAsString(aRequestBody));

        final var categoryId = this.mvc.perform(aRequest)
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse()
                .getHeader("Location").replace("/categories/", "");

        return CategoryID.from(categoryId);
    }

    private CategoryResponse retrieveCategory(final CategoryID categoryId) throws Exception {
        final var aRequest = get("/categories/{categoryId}", categoryId.getValue())
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);

        final var category = this.mvc.perform(aRequest)
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse()
                .getContentAsString();

        return Json.readValue(category, CategoryResponse.class);
    }

}
