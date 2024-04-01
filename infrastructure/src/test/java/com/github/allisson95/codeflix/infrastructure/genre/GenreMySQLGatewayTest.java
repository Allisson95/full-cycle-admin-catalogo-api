package com.github.allisson95.codeflix.infrastructure.genre;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;

import com.github.allisson95.codeflix.MySQLGatewayTest;
import com.github.allisson95.codeflix.domain.category.Category;
import com.github.allisson95.codeflix.domain.category.CategoryID;
import com.github.allisson95.codeflix.domain.genre.Genre;
import com.github.allisson95.codeflix.domain.genre.GenreID;
import com.github.allisson95.codeflix.domain.pagination.SearchQuery;
import com.github.allisson95.codeflix.infrastructure.category.CategoryMySQLGateway;
import com.github.allisson95.codeflix.infrastructure.genre.persistence.GenreJpaEntity;
import com.github.allisson95.codeflix.infrastructure.genre.persistence.GenreRepository;

@MySQLGatewayTest
class GenreMySQLGatewayTest {

    @Autowired
    private GenreMySQLGateway genreGateway;

    @Autowired
    private GenreRepository genreRepository;

    @Autowired
    private CategoryMySQLGateway categoryGateway;

    @Test
    void Given_AValidGenre_When_CallsCreateGenre_Should_PersisteGenre() {
        final var aCategory = Category.newCategory("Filmes", null, true);
        this.categoryGateway.create(aCategory);

        final var expectedName = "Ação";
        final var expectedIsActive = true;
        final var expectedCategories = List.of(aCategory.getId());

        final var aGenre = Genre.newGenre(expectedName, expectedIsActive);
        aGenre.addCategories(expectedCategories);

        final var expectedId = aGenre.getId();

        assertEquals(0, this.genreRepository.count());

        final var actualGenre = this.genreGateway.create(aGenre);

        assertEquals(1, this.genreRepository.count());

        assertEquals(expectedId, actualGenre.getId());
        assertEquals(expectedName, actualGenre.getName());
        assertEquals(expectedIsActive, actualGenre.isActive());
        assertEquals(expectedCategories, actualGenre.getCategories());
        assertEquals(aGenre.getCreatedAt(), actualGenre.getCreatedAt());
        assertEquals(aGenre.getUpdatedAt(), actualGenre.getUpdatedAt());
        assertEquals(aGenre.getDeletedAt(), actualGenre.getDeletedAt());
        assertNull(actualGenre.getDeletedAt());

        final var persistedGenre = genreRepository.findById(expectedId.getValue()).get();

        assertEquals(expectedName, persistedGenre.getName());
        assertEquals(expectedIsActive, persistedGenre.isActive());
        assertEquals(expectedCategories, persistedGenre.getCategoryIDs());
        assertEquals(aGenre.getCreatedAt(), persistedGenre.getCreatedAt());
        assertEquals(aGenre.getUpdatedAt(), persistedGenre.getUpdatedAt());
        assertEquals(aGenre.getDeletedAt(), persistedGenre.getDeletedAt());
        assertNull(persistedGenre.getDeletedAt());
    }

    @Test
    void Given_AValidGenreWithoutCategories_When_CallsCreateGenre_Should_PersisteGenre() {
        final var expectedName = "Ação";
        final var expectedIsActive = true;
        final var expectedCategories = List.<CategoryID>of();

        final var aGenre = Genre.newGenre(expectedName, expectedIsActive);

        final var expectedId = aGenre.getId();

        assertEquals(0, this.genreRepository.count());

        final var actualGenre = this.genreGateway.create(aGenre);

        assertEquals(1, this.genreRepository.count());

        assertEquals(expectedId, actualGenre.getId());
        assertEquals(expectedName, actualGenre.getName());
        assertEquals(expectedIsActive, actualGenre.isActive());
        assertEquals(expectedCategories, actualGenre.getCategories());
        assertEquals(aGenre.getCreatedAt(), actualGenre.getCreatedAt());
        assertEquals(aGenre.getUpdatedAt(), actualGenre.getUpdatedAt());
        assertEquals(aGenre.getDeletedAt(), actualGenre.getDeletedAt());
        assertNull(actualGenre.getDeletedAt());

        final var persistedGenre = genreRepository.findById(expectedId.getValue()).get();

        assertEquals(expectedName, persistedGenre.getName());
        assertEquals(expectedIsActive, persistedGenre.isActive());
        assertEquals(expectedCategories, persistedGenre.getCategoryIDs());
        assertEquals(aGenre.getCreatedAt(), persistedGenre.getCreatedAt());
        assertEquals(aGenre.getUpdatedAt(), persistedGenre.getUpdatedAt());
        assertEquals(aGenre.getDeletedAt(), persistedGenre.getDeletedAt());
        assertNull(persistedGenre.getDeletedAt());
    }

    @Test
    void Given_AValidGenreWithoutCategories_When_CallsUpdateGenreWithCategories_Should_PersisteGenre() {
        final var filmes = this.categoryGateway.create(Category.newCategory("Filmes", null, true));
        this.categoryGateway.create(filmes);
        final var series = this.categoryGateway.create(Category.newCategory("Séries", null, true));
        this.categoryGateway.create(series);

        final var expectedName = "Ação";
        final var expectedIsActive = true;
        final var expectedCategories = List.of(filmes.getId(), series.getId());

        final var aGenre = Genre.newGenre("ac", expectedIsActive);

        final var expectedId = aGenre.getId();

        assertEquals(0, this.genreRepository.count());

        genreRepository.saveAndFlush(GenreJpaEntity.from(aGenre));

        assertEquals("ac", aGenre.getName());
        assertEquals(0, aGenre.getCategories().size());

        final var actualGenre = this.genreGateway.update(
                Genre.with(aGenre)
                        .update(expectedName, expectedIsActive, expectedCategories));

        assertEquals(1, this.genreRepository.count());

        assertEquals(expectedId, actualGenre.getId());
        assertEquals(expectedName, actualGenre.getName());
        assertEquals(expectedIsActive, actualGenre.isActive());
        assertThat(actualGenre.getCategories(), containsInAnyOrder(expectedCategories.toArray()));
        assertEquals(aGenre.getCreatedAt(), actualGenre.getCreatedAt());
        assertTrue(aGenre.getUpdatedAt().isBefore(actualGenre.getUpdatedAt()));
        assertEquals(aGenre.getDeletedAt(), actualGenre.getDeletedAt());
        assertNull(actualGenre.getDeletedAt());

        final var persistedGenre = genreRepository.findById(expectedId.getValue()).get();

        assertEquals(expectedName, persistedGenre.getName());
        assertEquals(expectedIsActive, persistedGenre.isActive());
        assertThat(persistedGenre.getCategoryIDs(), containsInAnyOrder(expectedCategories.toArray()));
        assertEquals(aGenre.getCreatedAt(), persistedGenre.getCreatedAt());
        assertTrue(aGenre.getUpdatedAt().isBefore(persistedGenre.getUpdatedAt()));
        assertEquals(aGenre.getDeletedAt(), persistedGenre.getDeletedAt());
        assertNull(persistedGenre.getDeletedAt());
    }

    @Test
    void Given_AValidGenreWithCategories_When_CallsUpdateGenreCleaningCategories_Should_PersisteGenre() {
        final var filmes = Category.newCategory("Filmes", null, true);
        this.categoryGateway.create(filmes);
        final var series = Category.newCategory("Séries", null, true);
        this.categoryGateway.create(series);

        final var expectedName = "Ação";
        final var expectedIsActive = true;
        final var expectedCategories = List.<CategoryID>of();

        final var aGenre = Genre.newGenre("ac", expectedIsActive);
        aGenre.addCategories(List.of(filmes.getId(), series.getId()));

        final var expectedId = aGenre.getId();

        assertEquals(0, this.genreRepository.count());

        genreRepository.saveAndFlush(GenreJpaEntity.from(aGenre));

        assertEquals("ac", aGenre.getName());
        assertEquals(2, aGenre.getCategories().size());

        final var actualGenre = this.genreGateway.update(
                Genre.with(aGenre)
                        .update(expectedName, expectedIsActive, expectedCategories));

        assertEquals(1, this.genreRepository.count());

        assertEquals(expectedId, actualGenre.getId());
        assertEquals(expectedName, actualGenre.getName());
        assertEquals(expectedIsActive, actualGenre.isActive());
        assertEquals(expectedCategories, actualGenre.getCategories());
        assertEquals(aGenre.getCreatedAt(), actualGenre.getCreatedAt());
        assertTrue(aGenre.getUpdatedAt().isBefore(actualGenre.getUpdatedAt()));
        assertEquals(aGenre.getDeletedAt(), actualGenre.getDeletedAt());
        assertNull(actualGenre.getDeletedAt());

        final var persistedGenre = genreRepository.findById(expectedId.getValue()).get();

        assertEquals(expectedName, persistedGenre.getName());
        assertEquals(expectedIsActive, persistedGenre.isActive());
        assertEquals(expectedCategories, persistedGenre.getCategoryIDs());
        assertEquals(aGenre.getCreatedAt(), persistedGenre.getCreatedAt());
        assertTrue(aGenre.getUpdatedAt().isBefore(persistedGenre.getUpdatedAt()));
        assertEquals(aGenre.getDeletedAt(), persistedGenre.getDeletedAt());
        assertNull(persistedGenre.getDeletedAt());
    }

    @Test
    void Given_AValidActivatedGenre_When_CallsUpdateGenreActivating_Should_PersisteGenre() {
        final var expectedName = "Ação";
        final var expectedIsActive = true;
        final var expectedCategories = List.<CategoryID>of();

        final var aGenre = Genre.newGenre(expectedName, false);

        final var expectedId = aGenre.getId();

        assertEquals(0, this.genreRepository.count());

        genreRepository.saveAndFlush(GenreJpaEntity.from(aGenre));

        assertFalse(aGenre.isActive());
        assertNotNull(aGenre.getDeletedAt());

        final var actualGenre = this.genreGateway.update(
                Genre.with(aGenre)
                        .update(expectedName, expectedIsActive, expectedCategories));

        assertEquals(1, this.genreRepository.count());

        assertEquals(expectedId, actualGenre.getId());
        assertEquals(expectedName, actualGenre.getName());
        assertEquals(expectedIsActive, actualGenre.isActive());
        assertTrue(actualGenre.isActive());
        assertEquals(expectedCategories, actualGenre.getCategories());
        assertEquals(aGenre.getCreatedAt(), actualGenre.getCreatedAt());
        assertTrue(aGenre.getUpdatedAt().isBefore(actualGenre.getUpdatedAt()));
        assertNull(actualGenre.getDeletedAt());

        final var persistedGenre = genreRepository.findById(expectedId.getValue()).get();

        assertEquals(expectedName, persistedGenre.getName());
        assertEquals(expectedIsActive, persistedGenre.isActive());
        assertTrue(persistedGenre.isActive());
        assertEquals(expectedCategories, persistedGenre.getCategoryIDs());
        assertEquals(aGenre.getCreatedAt(), persistedGenre.getCreatedAt());
        assertTrue(aGenre.getUpdatedAt().isBefore(persistedGenre.getUpdatedAt()));
        assertNull(persistedGenre.getDeletedAt());
    }

    @Test
    void Given_AValidActivatedGenre_When_CallsUpdateGenreInactivating_Should_PersisteGenre() {
        final var expectedName = "Ação";
        final var expectedIsActive = false;
        final var expectedCategories = List.<CategoryID>of();

        final var aGenre = Genre.newGenre(expectedName, true);

        final var expectedId = aGenre.getId();

        assertEquals(0, this.genreRepository.count());

        genreRepository.saveAndFlush(GenreJpaEntity.from(aGenre));

        assertTrue(aGenre.isActive());
        assertNull(aGenre.getDeletedAt());

        final var actualGenre = this.genreGateway.update(
                Genre.with(aGenre)
                        .update(expectedName, expectedIsActive, expectedCategories));

        assertEquals(1, this.genreRepository.count());

        assertEquals(expectedId, actualGenre.getId());
        assertEquals(expectedName, actualGenre.getName());
        assertEquals(expectedIsActive, actualGenre.isActive());
        assertFalse(actualGenre.isActive());
        assertEquals(expectedCategories, actualGenre.getCategories());
        assertEquals(aGenre.getCreatedAt(), actualGenre.getCreatedAt());
        assertTrue(aGenre.getUpdatedAt().isBefore(actualGenre.getUpdatedAt()));
        assertNotNull(actualGenre.getDeletedAt());

        final var persistedGenre = genreRepository.findById(expectedId.getValue()).get();

        assertEquals(expectedName, persistedGenre.getName());
        assertEquals(expectedIsActive, persistedGenre.isActive());
        assertFalse(persistedGenre.isActive());
        assertEquals(expectedCategories, persistedGenre.getCategoryIDs());
        assertEquals(aGenre.getCreatedAt(), persistedGenre.getCreatedAt());
        assertTrue(aGenre.getUpdatedAt().isBefore(persistedGenre.getUpdatedAt()));
        assertNotNull(persistedGenre.getDeletedAt());
    }

    @Test
    void Given_APrePersistedGenre_When_CallsDeleteById_Should_DeleteGenre() {
        final var aGenre = Genre.newGenre("Ação", true);

        genreRepository.saveAndFlush(GenreJpaEntity.from(aGenre));

        assertEquals(1, this.genreRepository.count());

        this.genreGateway.deleteById(aGenre.getId());

        assertEquals(0, this.genreRepository.count());
    }

    @Test
    void Given_AnInvalidGenre_When_CallsDeleteById_Should_BeOK() {
        assertEquals(0, this.genreRepository.count());

        this.genreGateway.deleteById(GenreID.from("123"));

        assertEquals(0, this.genreRepository.count());
    }

    @Test
    void Given_APrePersistedGenre_When_CallsFindById_Should_ReturnGenre() {
        final var filmes = Category.newCategory("Filmes", null, true);
        this.categoryGateway.create(filmes);
        final var series = Category.newCategory("Séries", null, true);
        this.categoryGateway.create(series);

        final var expectedName = "Ação";
        final var expectedIsActive = true;
        final var expectedCategories = List.of(filmes.getId(), series.getId());

        final var aGenre = Genre.newGenre(expectedName, expectedIsActive);
        aGenre.addCategories(expectedCategories);

        final var expectedId = aGenre.getId();

        assertEquals(0, this.genreRepository.count());

        genreRepository.saveAndFlush(GenreJpaEntity.from(aGenre));

        assertEquals(1, this.genreRepository.count());

        final var actualGenre = this.genreGateway.findById(expectedId).get();

        assertEquals(1, this.genreRepository.count());

        assertEquals(expectedId, actualGenre.getId());
        assertEquals(expectedName, actualGenre.getName());
        assertEquals(expectedIsActive, actualGenre.isActive());
        assertThat(actualGenre.getCategories(), containsInAnyOrder(expectedCategories.toArray()));
        assertEquals(aGenre.getCreatedAt(), actualGenre.getCreatedAt());
        assertEquals(aGenre.getUpdatedAt(), actualGenre.getUpdatedAt());
        assertNull(actualGenre.getDeletedAt());

        final var persistedGenre = genreRepository.findById(expectedId.getValue()).get();

        assertEquals(expectedName, persistedGenre.getName());
        assertEquals(expectedIsActive, persistedGenre.isActive());
        assertThat(persistedGenre.getCategoryIDs(), containsInAnyOrder(expectedCategories.toArray()));
        assertEquals(aGenre.getCreatedAt(), persistedGenre.getCreatedAt());
        assertEquals(aGenre.getUpdatedAt(), persistedGenre.getUpdatedAt());
        assertNull(persistedGenre.getDeletedAt());
    }

    @Test
    void Given_AInvalidGenreID_When_CallsFindById_Should_ReturnEmpty() {
        assertEquals(0, this.genreRepository.count());

        final var actualGenre = this.genreGateway.findById(GenreID.from("empty"));

        assertEquals(0, this.genreRepository.count());

        assertTrue(actualGenre.isEmpty());
    }

    @Test
    void Given_EmptyGenres_When_CallsFindAll_Should_ReturnAEmptyList() {
        final var expectedPage = 0;
        final var expectedPerPage = 1;
        final var expectedTerms = "";
        final var expectedSort = "name";
        final var expectedDirection = "asc";
        final var expectedTotal = 0;

        final var aQuery = new SearchQuery(
                expectedPage,
                expectedPerPage,
                expectedTerms,
                expectedSort,
                expectedDirection);

        final var actualPage = genreGateway.findAll(aQuery);

        assertEquals(expectedPage, actualPage.currentPage());
        assertEquals(expectedPerPage, actualPage.perPage());
        assertEquals(expectedTotal, actualPage.total());
        assertEquals(expectedTotal, actualPage.items().size());
    }

    @ParameterizedTest
    @CsvSource({
            "aç,0,10,1,1,Ação",
            "dr,0,10,1,1,Drama",
            "com,0,10,1,1,Comédia Romântica",
            "cien,0,10,1,1,Ficção Científica",
            "terr,0,10,1,1,Terror",
    })
    void Given_AValidTerms_When_CallsFindAll_Should_ReturnFiltered(
            final String expectedTerms,
            final int expectedPage,
            final int expectedPerPage,
            final int expectedItemsCount,
            final long expectedTotal,
            final String expectedGenreName) {

        this.mockGenres();

        final var expectedSort = "name";
        final var expectedDirection = "asc";

        final var aQuery = new SearchQuery(
                expectedPage,
                expectedPerPage,
                expectedTerms,
                expectedSort,
                expectedDirection);

        final var actualPage = genreGateway.findAll(aQuery);

        assertEquals(expectedPage, actualPage.currentPage());
        assertEquals(expectedPerPage, actualPage.perPage());
        assertEquals(expectedTotal, actualPage.total());
        assertEquals(expectedItemsCount, actualPage.items().size());
        assertEquals(expectedGenreName, actualPage.items().get(0).getName());
    }

    @ParameterizedTest
    @CsvSource({
            "name,asc,0,10,5,5,Ação",
            "name,desc,0,10,5,5,Terror",
            "createdAt,asc,0,10,5,5,Comédia Romântica",
            "createdAt,desc,0,10,5,5,Ficção Científica",
    })
    void Given_AValidSortAndDirection_When_CallsFindAll_Should_ReturnOrdered(
            final String expectedSort,
            final String expectedDirection,
            final int expectedPage,
            final int expectedPerPage,
            final int expectedItemsCount,
            final long expectedTotal,
            final String expectedGenreName) {

        this.mockGenres();

        final var expectedTerms = "";

        final var aQuery = new SearchQuery(
                expectedPage,
                expectedPerPage,
                expectedTerms,
                expectedSort,
                expectedDirection);

        final var actualPage = genreGateway.findAll(aQuery);

        assertEquals(expectedPage, actualPage.currentPage());
        assertEquals(expectedPerPage, actualPage.perPage());
        assertEquals(expectedTotal, actualPage.total());
        assertEquals(expectedItemsCount, actualPage.items().size());
        assertEquals(expectedGenreName, actualPage.items().get(0).getName());
    }

    @ParameterizedTest
    @CsvSource({
            "0,2,2,5,Ação;Comédia Romântica",
            "1,2,2,5,Drama;Ficção Científica",
            "2,2,1,5,Terror",
    })
    void Given_AValidPage_When_CallsFindAll_Should_ReturnPaginated(
            final int expectedPage,
            final int expectedPerPage,
            final int expectedItemsCount,
            final long expectedTotal,
            final String expectedGenres) {

        this.mockGenres();

        final var expectedTerms = "";
        final var expectedSort = "name";
        final var expectedDirection = "asc";

        final var aQuery = new SearchQuery(
                expectedPage,
                expectedPerPage,
                expectedTerms,
                expectedSort,
                expectedDirection);

        final var actualPage = genreGateway.findAll(aQuery);

        assertEquals(expectedPage, actualPage.currentPage());
        assertEquals(expectedPerPage, actualPage.perPage());
        assertEquals(expectedTotal, actualPage.total());
        assertEquals(expectedItemsCount, actualPage.items().size());

        final var expectedGenresName = expectedGenres.split(";");
        for (int i = 0; i < expectedGenresName.length; i++) {
            assertEquals(expectedGenresName[i], actualPage.items().get(i).getName());
        }
    }

    private void mockGenres() {
        this.genreRepository.saveAllAndFlush(List.of(
                GenreJpaEntity.from(Genre.newGenre("Comédia Romântica", true)),
                GenreJpaEntity.from(Genre.newGenre("Ação", true)),
                GenreJpaEntity.from(Genre.newGenre("Drama", true)),
                GenreJpaEntity.from(Genre.newGenre("Terror", true)),
                GenreJpaEntity.from(Genre.newGenre("Ficção Científica", true))));
    }

}
