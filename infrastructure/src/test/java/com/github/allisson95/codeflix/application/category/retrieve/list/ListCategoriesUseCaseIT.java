package com.github.allisson95.codeflix.application.category.retrieve.list;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;

import com.github.allisson95.codeflix.IntegrationTest;
import com.github.allisson95.codeflix.domain.category.Category;
import com.github.allisson95.codeflix.domain.category.CategorySearchQuery;
import com.github.allisson95.codeflix.infrastructure.category.persistence.CategoryJpaEntity;
import com.github.allisson95.codeflix.infrastructure.category.persistence.CategoryRepository;

@IntegrationTest
class ListCategoriesUseCaseIT {

    @Autowired
    private ListCategoriesUseCase useCase;

    @Autowired
    private CategoryRepository categoryRepository;

    @BeforeEach
    void mockUp() {
        final var categories = Stream.of(
                    Category.newCategory("Filmes", null, true),
                    Category.newCategory("Séries", null, true),
                    Category.newCategory("Documentários", null, true),
                    Category.newCategory("Kids", "Conteúdo para crianças", true),
                    Category.newCategory("Esportes", null, true),
                    Category.newCategory("Netflix Originals", "Títulos originais da Netflix", true),
                    Category.newCategory("Amazon Originals", "Títulos originais da Amazon", true)
                )
                .map(CategoryJpaEntity::from)
                .toList();

        categoryRepository.saveAllAndFlush(categories);
    }

    @Test
    void Given_AValidTerm_When_TermDoesntMatchesPrePersistedCategories_Should_ReturnEmptyPage() {
        final var expectedPage = 0;
        final var expectedPerPage = 10;
        final var expectedTerms = "empty";
        final var expectedSort = "name";
        final var expectedDirection = "asc";
        final var expectedItemsCount = 0;
        final var expectedTotal = 0;

        final var aQuery = new CategorySearchQuery(expectedPage, expectedPerPage, expectedTerms, expectedSort, expectedDirection);

        final var categoriesPage = useCase.execute(aQuery);

        assertEquals(expectedItemsCount, categoriesPage.items().size());
        assertEquals(expectedPage, categoriesPage.currentPage());
        assertEquals(expectedPerPage, categoriesPage.perPage());
        assertEquals(expectedTotal, categoriesPage.total());
    }

    @ParameterizedTest
    @CsvSource({
            "fil,0,10,1,1,Filmes",
            "net,0,10,1,1,Netflix Originals",
            "ZON,0,10,1,1,Amazon Originals",
            "KI,0,10,1,1,Kids",
            "crianças,0,10,1,1,Kids",
            "da Amazon,0,10,1,1,Amazon Originals",
    })
    void Given_AValidTerm_When_CallsListCategories_Should_ReturnCategoriesFiltered(
            final String expectedTerms,
            final int expectedPage,
            final int expectedPerPage,
            final int expectedItemsCount,
            final long expectedTotal,
            final String expectedCategoryName
    ) {
        final var expectedSort = "name";
        final var expectedDirection = "asc";

        final var aQuery = new CategorySearchQuery(expectedPage, expectedPerPage, expectedTerms, expectedSort, expectedDirection);

        final var categoriesPage = useCase.execute(aQuery);

        assertEquals(expectedItemsCount, categoriesPage.items().size());
        assertEquals(expectedPage, categoriesPage.currentPage());
        assertEquals(expectedPerPage, categoriesPage.perPage());
        assertEquals(expectedTotal, categoriesPage.total());
        assertEquals(expectedCategoryName, categoriesPage.items().get(0).name());
    }

    @ParameterizedTest
    @CsvSource({
            "name,asc,0,10,7,7,Amazon Originals",
            "name,desc,0,10,7,7,Séries",
            "createdAt,asc,0,10,7,7,Filmes",
            "createdAt,desc,0,10,7,7,Amazon Originals",
    })
    void Given_AValidSortAndDirection_When_CallsListCategories_Should_ReturnCategoriesOrdered(
            final String expectedSort,
            final String expectedDirection,
            final int expectedPage,
            final int expectedPerPage,
            final int expectedItemsCount,
            final long expectedTotal,
            final String expectedCategoryName
    ) {
        final var expectedTerms = "";

        final var aQuery = new CategorySearchQuery(expectedPage, expectedPerPage, expectedTerms, expectedSort, expectedDirection);

        final var categoriesPage = useCase.execute(aQuery);

        assertEquals(expectedItemsCount, categoriesPage.items().size());
        assertEquals(expectedPage, categoriesPage.currentPage());
        assertEquals(expectedPerPage, categoriesPage.perPage());
        assertEquals(expectedTotal, categoriesPage.total());
        assertEquals(expectedCategoryName, categoriesPage.items().get(0).name());
    }

    @ParameterizedTest
    @CsvSource({
            "0,2,2,7,Amazon Originals;Documentários",
            "1,2,2,7,Esportes;Filmes",
            "2,2,2,7,Kids;Netflix Originals",
            "3,2,1,7,Séries",
    })
    void Given_AValidPage_When_CallsListCategories_Should_ReturnCategoriesPaginated(
            final int expectedPage,
            final int expectedPerPage,
            final int expectedItemsCount,
            final long expectedTotal,
            final String expectedCategoriesName
    ) {
        final var expectedTerms = "";
        final var expectedSort = "name";
        final var expectedDirection = "asc";

        final var aQuery = new CategorySearchQuery(expectedPage, expectedPerPage, expectedTerms, expectedSort, expectedDirection);

        final var categoriesPage = useCase.execute(aQuery);

        assertEquals(expectedItemsCount, categoriesPage.items().size());
        assertEquals(expectedPage, categoriesPage.currentPage());
        assertEquals(expectedPerPage, categoriesPage.perPage());
        assertEquals(expectedTotal, categoriesPage.total());

        final var expectedCategoriesNameSplitted = expectedCategoriesName.split(";");

        for (int i = 0; i < expectedCategoriesNameSplitted.length; i++) {
            final var expectedCategoryName = expectedCategoriesNameSplitted[i];
            assertEquals(expectedCategoryName, categoriesPage.items().get(i).name());
        }
    }

}
