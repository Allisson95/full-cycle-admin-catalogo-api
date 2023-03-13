package com.github.allisson95.codeflix.infrastructure.category;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.github.allisson95.codeflix.MySQLGatewayTest;
import com.github.allisson95.codeflix.domain.category.Category;
import com.github.allisson95.codeflix.domain.category.CategoryID;
import com.github.allisson95.codeflix.domain.pagination.SearchQuery;
import com.github.allisson95.codeflix.infrastructure.category.persistence.CategoryJpaEntity;
import com.github.allisson95.codeflix.infrastructure.category.persistence.CategoryRepository;

@MySQLGatewayTest
class CategoryMySQLGatewayTest {

    @Autowired
    private CategoryMySQLGateway categoryGateway;

    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    void Given_AValidCategory_When_CallCreate_Then_ReturnANewCategory() {
        final var expectedName = "Filme";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;

        final var aCategory = Category.newCategory(expectedName, expectedDescription, expectedIsActive);

        assertEquals(0, categoryRepository.count());

        final var createdCategory = categoryGateway.create(aCategory);

        assertEquals(1, categoryRepository.count());

        assertEquals(aCategory.getId(), createdCategory.getId());
        assertEquals(expectedName, createdCategory.getName());
        assertEquals(expectedDescription, createdCategory.getDescription());
        assertEquals(expectedIsActive, createdCategory.isActive());
        assertEquals(aCategory.getCreatedAt(), createdCategory.getCreatedAt());
        assertEquals(aCategory.getUpdatedAt(), createdCategory.getUpdatedAt());
        assertEquals(aCategory.getDeletedAt(), createdCategory.getDeletedAt());
        assertNull(createdCategory.getDeletedAt());

        final var createdCategoryJpaEntity = categoryRepository.findById(aCategory.getId().getValue()).get();

        assertEquals(aCategory.getId().getValue(), createdCategoryJpaEntity.getId());
        assertEquals(expectedName, createdCategoryJpaEntity.getName());
        assertEquals(expectedDescription, createdCategoryJpaEntity.getDescription());
        assertEquals(expectedIsActive, createdCategoryJpaEntity.isActive());
        assertEquals(aCategory.getCreatedAt(), createdCategoryJpaEntity.getCreatedAt());
        assertEquals(aCategory.getUpdatedAt(), createdCategoryJpaEntity.getUpdatedAt());
        assertEquals(aCategory.getDeletedAt(), createdCategoryJpaEntity.getDeletedAt());
        assertNull(createdCategoryJpaEntity.getDeletedAt());
    }

    @Test
    void Given_AValidCategory_When_CallUpdate_Then_ReturnUpdatedCategory() {
        final var expectedName = "Filme";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;

        final var aCategory = Category.newCategory("Film", null, false);

        assertEquals(0, categoryRepository.count());

        categoryRepository.saveAndFlush(CategoryJpaEntity.from(aCategory));

        assertEquals(1, categoryRepository.count());

        final var invalidCategoryJpaEntity = categoryRepository.findById(aCategory.getId().getValue()).get();

        assertEquals(aCategory.getId().getValue(), invalidCategoryJpaEntity.getId());
        assertEquals(aCategory.getName(), invalidCategoryJpaEntity.getName());
        assertEquals(aCategory.getDescription(), invalidCategoryJpaEntity.getDescription());
        assertEquals(aCategory.isActive(), invalidCategoryJpaEntity.isActive());
        assertEquals(aCategory.getCreatedAt(), invalidCategoryJpaEntity.getCreatedAt());
        assertEquals(aCategory.getUpdatedAt(), invalidCategoryJpaEntity.getUpdatedAt());
        assertEquals(aCategory.getDeletedAt(), invalidCategoryJpaEntity.getDeletedAt());
        assertNotNull(invalidCategoryJpaEntity.getDeletedAt());

        final var anUpdatedCategory = Category.with(aCategory)
                .update(expectedName, expectedDescription, expectedIsActive);

        final var updatedCategory = categoryGateway.update(anUpdatedCategory);

        assertEquals(1, categoryRepository.count());

        assertEquals(aCategory.getId(), updatedCategory.getId());
        assertEquals(expectedName, updatedCategory.getName());
        assertEquals(expectedDescription, updatedCategory.getDescription());
        assertEquals(expectedIsActive, updatedCategory.isActive());
        assertEquals(aCategory.getCreatedAt(), updatedCategory.getCreatedAt());
        assertTrue(aCategory.getUpdatedAt().isBefore(updatedCategory.getUpdatedAt()));
        assertNull(updatedCategory.getDeletedAt());

        final var createdCategoryJpaEntity = categoryRepository.findById(aCategory.getId().getValue()).get();

        assertEquals(aCategory.getId().getValue(), createdCategoryJpaEntity.getId());
        assertEquals(expectedName, createdCategoryJpaEntity.getName());
        assertEquals(expectedDescription, createdCategoryJpaEntity.getDescription());
        assertEquals(expectedIsActive, createdCategoryJpaEntity.isActive());
        assertEquals(aCategory.getCreatedAt(), createdCategoryJpaEntity.getCreatedAt());
        assertTrue(aCategory.getUpdatedAt().isBefore(createdCategoryJpaEntity.getUpdatedAt()));
        assertNull(createdCategoryJpaEntity.getDeletedAt());
    }

    @Test
    void Given_APrePersistedCategoryAndValidCategoryId_When_TryToDeleteIt_Then_DeleteCategory() {
        final var aCategory = Category.newCategory("Filme", null, true);

        assertEquals(0, categoryRepository.count());

        categoryRepository.saveAndFlush(CategoryJpaEntity.from(aCategory));

        assertEquals(1, categoryRepository.count());

        categoryGateway.deleteById(aCategory.getId());

        assertEquals(0, categoryRepository.count());
    }

    @Test
    void Given_AnIvalidCategoryId_When_TryToDeleteIt_Then_DeleteCategory() {
        assertEquals(0, categoryRepository.count());

        categoryGateway.deleteById(CategoryID.from("invalid"));

        assertEquals(0, categoryRepository.count());
    }

    @Test
    void Given_APrePersistedCategoryAndValidCategoryId_When_CallFindById_Then_ReturnCategory() {
        final var expectedName = "Filme";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;

        final var aCategory = Category.newCategory(expectedName, expectedDescription, expectedIsActive);

        assertEquals(0, categoryRepository.count());

        categoryRepository.saveAndFlush(CategoryJpaEntity.from(aCategory));

        assertEquals(1, categoryRepository.count());

        final var actualCategory = categoryGateway.findById(aCategory.getId()).get();

        assertEquals(1, categoryRepository.count());

        assertEquals(aCategory.getId(), actualCategory.getId());
        assertEquals(expectedName, actualCategory.getName());
        assertEquals(expectedDescription, actualCategory.getDescription());
        assertEquals(expectedIsActive, actualCategory.isActive());
        assertEquals(aCategory.getCreatedAt(), actualCategory.getCreatedAt());
        assertEquals(aCategory.getUpdatedAt(), actualCategory.getUpdatedAt());
        assertEquals(aCategory.getDeletedAt(), actualCategory.getDeletedAt());
    }

    @Test
    void Given_ACategoryIdNotStored_When_CallFindById_Then_ReturnEmpty() {
        assertEquals(0, categoryRepository.count());

        final var actualCategory = categoryGateway.findById(CategoryID.from("empty"));

        assertEquals(0, categoryRepository.count());

        assertTrue(actualCategory.isEmpty());
    }

    @Test
    void Given_PrePersistedCategories_When_CallFindAll_Then_ReturnPaginatedCategories() {
        final var expectedPage = 0;
        final var expectedPerPage = 1;
        final var expectedTotal = 3;

        final var filmes = Category.newCategory("Filmes", null, true);
        final var series = Category.newCategory("Séries", null, true);
        final var documentarios = Category.newCategory("Documentários", null, true);

        assertEquals(0, categoryRepository.count());

        categoryRepository.saveAllAndFlush(List.of(
                CategoryJpaEntity.from(filmes),
                CategoryJpaEntity.from(series),
                CategoryJpaEntity.from(documentarios)));

        assertEquals(3, categoryRepository.count());

        final var aQuery = new SearchQuery(0, 1, "", "name", "asc");
        final var pageCategories = categoryGateway.findAll(aQuery);

        assertEquals(3, categoryRepository.count());

        assertEquals(expectedPage, pageCategories.currentPage());
        assertEquals(expectedPerPage, pageCategories.perPage());
        assertEquals(expectedTotal, pageCategories.total());
        assertEquals(expectedPerPage, pageCategories.items().size());
        assertEquals(documentarios.getId(), pageCategories.items().get(0).getId());
    }

    @Test
    void Given_NoStoredCategories_When_CallFindAll_Then_ReturnEmptyPageCategories() {
        final var expectedPage = 0;
        final var expectedPerPage = 1;
        final var expectedTotal = 0;

        assertEquals(0, categoryRepository.count());

        final var aQuery = new SearchQuery(0, 1, "", "name", "asc");
        final var pageCategories = categoryGateway.findAll(aQuery);

        assertEquals(0, categoryRepository.count());

        assertEquals(expectedPage, pageCategories.currentPage());
        assertEquals(expectedPerPage, pageCategories.perPage());
        assertEquals(expectedTotal, pageCategories.total());
        assertEquals(expectedTotal, pageCategories.items().size());
    }

    @Test
    void Given_PrePersistedCategories_When_CallFindAllWithDirectionDesc_Then_ReturnDescendingPaginatedCategories() {
        final var expectedPage = 0;
        final var expectedPerPage = 1;
        final var expectedTotal = 3;

        final var filmes = Category.newCategory("Filmes", null, true);
        final var series = Category.newCategory("Séries", null, true);
        final var documentarios = Category.newCategory("Documentários", null, true);

        assertEquals(0, categoryRepository.count());

        categoryRepository.saveAllAndFlush(List.of(
                CategoryJpaEntity.from(filmes),
                CategoryJpaEntity.from(series),
                CategoryJpaEntity.from(documentarios)));

        assertEquals(3, categoryRepository.count());

        final var aQuery = new SearchQuery(0, 1, "", "name", "desc");
        final var pageCategories = categoryGateway.findAll(aQuery);

        assertEquals(3, categoryRepository.count());

        assertEquals(expectedPage, pageCategories.currentPage());
        assertEquals(expectedPerPage, pageCategories.perPage());
        assertEquals(expectedTotal, pageCategories.total());
        assertEquals(expectedPerPage, pageCategories.items().size());
        assertEquals(series.getId(), pageCategories.items().get(0).getId());
    }

    @Test
    void Given_MoreThanOnePage_When_CallFindAllWithNextPage_Then_ReturnNextPaginatedCategories() {
        var expectedPage = 0;
        final var expectedPerPage = 1;
        final var expectedTotal = 3;

        final var filmes = Category.newCategory("Filmes", null, true);
        final var series = Category.newCategory("Séries", null, true);
        final var documentarios = Category.newCategory("Documentários", null, true);

        assertEquals(0, categoryRepository.count());

        categoryRepository.saveAllAndFlush(List.of(
                CategoryJpaEntity.from(filmes),
                CategoryJpaEntity.from(series),
                CategoryJpaEntity.from(documentarios)));

        assertEquals(3, categoryRepository.count());

        var aQuery = new SearchQuery(0, 1, "", "name", "asc");
        var pageCategories = categoryGateway.findAll(aQuery);

        assertEquals(3, categoryRepository.count());

        assertEquals(expectedPage, pageCategories.currentPage());
        assertEquals(expectedPerPage, pageCategories.perPage());
        assertEquals(expectedTotal, pageCategories.total());
        assertEquals(expectedPerPage, pageCategories.items().size());
        assertEquals(documentarios.getId(), pageCategories.items().get(0).getId());

        // Page 1
        expectedPage = 1;

        aQuery = new SearchQuery(1, 1, "", "name", "asc");
        pageCategories = categoryGateway.findAll(aQuery);

        assertEquals(3, categoryRepository.count());

        assertEquals(expectedPage, pageCategories.currentPage());
        assertEquals(expectedPerPage, pageCategories.perPage());
        assertEquals(expectedTotal, pageCategories.total());
        assertEquals(expectedPerPage, pageCategories.items().size());
        assertEquals(filmes.getId(), pageCategories.items().get(0).getId());

        // Page 2
        expectedPage = 2;

        aQuery = new SearchQuery(2, 1, "", "name", "asc");
        pageCategories = categoryGateway.findAll(aQuery);

        assertEquals(3, categoryRepository.count());

        assertEquals(expectedPage, pageCategories.currentPage());
        assertEquals(expectedPerPage, pageCategories.perPage());
        assertEquals(expectedTotal, pageCategories.total());
        assertEquals(expectedPerPage, pageCategories.items().size());
        assertEquals(series.getId(), pageCategories.items().get(0).getId());
    }

    @Test
    void Given_PrePersistedCategories_When_CallFindAllWithDocAsTerms_Then_ReturnPaginatedCategoriesWithNameMatches() {
        final var expectedPage = 0;
        final var expectedPerPage = 1;
        final var expectedTotal = 1;

        final var filmes = Category.newCategory("Filmes", null, true);
        final var series = Category.newCategory("Séries", null, true);
        final var documentarios = Category.newCategory("Documentários", null, true);

        assertEquals(0, categoryRepository.count());

        categoryRepository.saveAllAndFlush(List.of(
                CategoryJpaEntity.from(filmes),
                CategoryJpaEntity.from(series),
                CategoryJpaEntity.from(documentarios)));

        assertEquals(3, categoryRepository.count());

        final var aQuery = new SearchQuery(0, 1, "doc", "name", "asc");
        final var pageCategories = categoryGateway.findAll(aQuery);

        assertEquals(3, categoryRepository.count());

        assertEquals(expectedPage, pageCategories.currentPage());
        assertEquals(expectedPerPage, pageCategories.perPage());
        assertEquals(expectedTotal, pageCategories.total());
        assertEquals(expectedPerPage, pageCategories.items().size());
        assertEquals(documentarios.getId(), pageCategories.items().get(0).getId());
    }

    @Test
    void Given_PrePersistedCategories_When_CallFindAllWithMaisAssistidaAsTerms_Then_ReturnPaginatedCategoriesWithDescriptionMatches() {
        final var expectedPage = 0;
        final var expectedPerPage = 1;
        final var expectedTotal = 1;

        final var filmes = Category.newCategory("Filmes", "A categoria mais assistida", true);
        final var series = Category.newCategory("Séries", "Uma categoria pouco assistida", true);
        final var documentarios = Category.newCategory("Documentários", "A categoria menos assistida", true);

        assertEquals(0, categoryRepository.count());

        categoryRepository.saveAllAndFlush(List.of(
                CategoryJpaEntity.from(filmes),
                CategoryJpaEntity.from(series),
                CategoryJpaEntity.from(documentarios)));

        assertEquals(3, categoryRepository.count());

        final var aQuery = new SearchQuery(0, 1, "MAIS ASSISTIDA", "name", "asc");
        final var pageCategories = categoryGateway.findAll(aQuery);

        assertEquals(3, categoryRepository.count());

        assertEquals(expectedPage, pageCategories.currentPage());
        assertEquals(expectedPerPage, pageCategories.perPage());
        assertEquals(expectedTotal, pageCategories.total());
        assertEquals(expectedPerPage, pageCategories.items().size());
        assertEquals(filmes.getId(), pageCategories.items().get(0).getId());
    }

    @Test
    void Given_PrePersistedCategories_When_CallsExistsByIds_Then_ReturnIds() {
        final var filmes = Category.newCategory("Filmes", null, true);
        final var series = Category.newCategory("Séries", null, true);
        final var documentarios = Category.newCategory("Documentários", null, true);

        final var expectedIds = List.of(filmes.getId(), series.getId());

        assertEquals(0, categoryRepository.count());

        categoryRepository.saveAllAndFlush(List.of(
                CategoryJpaEntity.from(filmes),
                CategoryJpaEntity.from(series),
                CategoryJpaEntity.from(documentarios)));

        assertEquals(3, categoryRepository.count());

        final var actualIds = categoryGateway.existsByIds(List.of(
                filmes.getId(),
                series.getId(),
                CategoryID.from("123")));

        assertEquals(3, categoryRepository.count());

        assertThat(actualIds, containsInAnyOrder(expectedIds.toArray()));
    }

}
