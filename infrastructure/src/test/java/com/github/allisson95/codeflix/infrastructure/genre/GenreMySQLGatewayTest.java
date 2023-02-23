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
import org.springframework.beans.factory.annotation.Autowired;

import com.github.allisson95.codeflix.MySQLGatewayTest;
import com.github.allisson95.codeflix.domain.category.Category;
import com.github.allisson95.codeflix.domain.category.CategoryID;
import com.github.allisson95.codeflix.domain.genre.Genre;
import com.github.allisson95.codeflix.domain.genre.GenreID;
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
        assertThat(actualGenre.getCategories(), containsInAnyOrder(persistedGenre.getCategoryIDs().toArray()));
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

}
