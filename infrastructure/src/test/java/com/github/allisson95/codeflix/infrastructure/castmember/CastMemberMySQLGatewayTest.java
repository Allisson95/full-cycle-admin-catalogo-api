package com.github.allisson95.codeflix.infrastructure.castmember;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;

import com.github.allisson95.codeflix.domain.Fixture;
import com.github.allisson95.codeflix.MySQLGatewayTest;
import com.github.allisson95.codeflix.domain.castmember.CastMember;
import com.github.allisson95.codeflix.domain.castmember.CastMemberID;
import com.github.allisson95.codeflix.domain.castmember.CastMemberType;
import com.github.allisson95.codeflix.domain.pagination.SearchQuery;
import com.github.allisson95.codeflix.infrastructure.castmember.persistence.CastMemberJpaEntity;
import com.github.allisson95.codeflix.infrastructure.castmember.persistence.CastMemberRepository;

@MySQLGatewayTest
class CastMemberMySQLGatewayTest {

    @Autowired
    private CastMemberMySQLGateway castMemberMySQLGateway;

    @Autowired
    private CastMemberRepository castMemberRepository;

    @Test
    void testDependencies() {
        assertNotNull(castMemberMySQLGateway);
        assertNotNull(castMemberRepository);
    }

    @Test
    void Given_AValidCastMember_When_CallsCreate_Should_PersistIt() {
        final var expectedName = Fixture.name();
        final var expectedType = Fixture.CastMembers.type();

        final var aMember = CastMember.newMember(expectedName, expectedType);
        final var expectedId = aMember.getId();

        assertEquals(0, this.castMemberRepository.count());

        final var actualMember = this.castMemberMySQLGateway.create(CastMember.with(aMember));

        assertEquals(1, this.castMemberRepository.count());

        assertEquals(expectedId, actualMember.getId());
        assertEquals(expectedName, actualMember.getName());
        assertEquals(expectedType, actualMember.getType());
        assertEquals(aMember.getCreatedAt(), actualMember.getCreatedAt());
        assertEquals(aMember.getUpdatedAt(), actualMember.getUpdatedAt());

        final var persistedMember = this.castMemberRepository.findById(expectedId.getValue()).get();

        assertEquals(expectedId.getValue(), persistedMember.getId());
        assertEquals(expectedName, persistedMember.getName());
        assertEquals(expectedType, persistedMember.getType());
        assertEquals(aMember.getCreatedAt(), persistedMember.getCreatedAt());
        assertEquals(aMember.getUpdatedAt(), persistedMember.getUpdatedAt());
    }

    @Test
    void Given_AValidCastMember_When_CallsUpdate_Should_RefreshIt() {
        final var expectedName = Fixture.name();
        final var expectedType = CastMemberType.ACTOR;

        final var aMember = CastMember.newMember("vind", CastMemberType.DIRECTOR);
        final var expectedId = aMember.getId();

        final var prePersistedMember = this.castMemberRepository.saveAndFlush(CastMemberJpaEntity.from(aMember));

        assertEquals(1, this.castMemberRepository.count());

        assertEquals(expectedId.getValue(), prePersistedMember.getId());
        assertEquals("vind", prePersistedMember.getName());
        assertEquals(CastMemberType.DIRECTOR, prePersistedMember.getType());

        final var actualMember = this.castMemberMySQLGateway
                .update(CastMember.with(aMember).update(expectedName, expectedType));

        assertEquals(1, this.castMemberRepository.count());

        assertEquals(expectedId, actualMember.getId());
        assertEquals(expectedName, actualMember.getName());
        assertEquals(expectedType, actualMember.getType());
        assertEquals(aMember.getCreatedAt(), actualMember.getCreatedAt());
        assertTrue(aMember.getUpdatedAt().isBefore(actualMember.getUpdatedAt()));

        final var persistedMember = this.castMemberRepository.findById(expectedId.getValue()).get();

        assertEquals(expectedId.getValue(), persistedMember.getId());
        assertEquals(expectedName, persistedMember.getName());
        assertEquals(expectedType, persistedMember.getType());
        assertEquals(aMember.getCreatedAt(), persistedMember.getCreatedAt());
        assertTrue(aMember.getUpdatedAt().isBefore(persistedMember.getUpdatedAt()));
    }

    @Test
    void Given_AValidCastMemberID_When_CallsDeleteById_Should_RemoveIt() {
        final var aMember = CastMember.newMember(Fixture.name(), Fixture.CastMembers.type());
        final var expectedId = aMember.getId();

        this.castMemberRepository.saveAndFlush(CastMemberJpaEntity.from(aMember));

        assertEquals(1, this.castMemberRepository.count());

        this.castMemberMySQLGateway.deleteById(expectedId);

        assertEquals(0, this.castMemberRepository.count());
    }

    @Test
    void Given_AInvalidCastMemberID_When_CallsDeleteById_Should_DoNothing() {
        final var aMember = CastMember.newMember(Fixture.name(), Fixture.CastMembers.type());

        this.castMemberRepository.saveAndFlush(CastMemberJpaEntity.from(aMember));

        assertEquals(1, this.castMemberRepository.count());

        this.castMemberMySQLGateway.deleteById(CastMemberID.from("123"));

        assertEquals(1, this.castMemberRepository.count());
    }

    @Test
    void Given_AValidCastMemberID_When_CallsFindById_Should_ReturnIt() {
        final var expectedName = Fixture.name();
        final var expectedType = Fixture.CastMembers.type();
        final var aMember = CastMember.newMember(expectedName, expectedType);
        final var expectedId = aMember.getId();

        final var prePersistedMember = this.castMemberRepository.saveAndFlush(CastMemberJpaEntity.from(aMember));

        assertEquals(1, this.castMemberRepository.count());

        assertEquals(expectedId.getValue(), prePersistedMember.getId());
        assertEquals(expectedName, prePersistedMember.getName());
        assertEquals(expectedType, prePersistedMember.getType());
        assertEquals(aMember.getCreatedAt(), prePersistedMember.getCreatedAt());
        assertEquals(aMember.getUpdatedAt(), prePersistedMember.getUpdatedAt());

        final var actualMember = this.castMemberMySQLGateway.findById(expectedId).get();

        assertEquals(expectedId, actualMember.getId());
        assertEquals(expectedName, actualMember.getName());
        assertEquals(expectedType, actualMember.getType());
        assertEquals(aMember.getCreatedAt(), actualMember.getCreatedAt());
        assertEquals(aMember.getUpdatedAt(), actualMember.getUpdatedAt());
    }

    @Test
    void Given_AInvalidCastMemberID_When_CallsFindById_Should_ReturnEmpty() {
        final var aMember = CastMember.newMember(Fixture.name(), Fixture.CastMembers.type());

        this.castMemberRepository.saveAndFlush(CastMemberJpaEntity.from(aMember));

        assertEquals(1, this.castMemberRepository.count());

        final var actualMember = this.castMemberMySQLGateway.findById(CastMemberID.from("123"));

        assertTrue(actualMember.isEmpty());
    }

    @Test
    void Given_EmptyCastMembers_When_CallsFindAll_Should_ReturnEmpty() {
        final var expectedPage = 0;
        final var expectedPerPage = 10;
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

        assertEquals(0, this.castMemberRepository.count());

        final var actualPage = this.castMemberMySQLGateway.findAll(aQuery);

        assertEquals(0, this.castMemberRepository.count());

        assertNotNull(actualPage);
        assertEquals(expectedPage, actualPage.currentPage());
        assertEquals(expectedPerPage, actualPage.perPage());
        assertEquals(expectedTotal, actualPage.total());
        assertEquals(expectedTotal, actualPage.items().size());
    }

    @ParameterizedTest
    @CsvSource({
        "ste,0,10,1,1,Steven Spielberg",
        "nic,0,10,1,1,Nicolas Cage",
        "ith,0,10,1,1,Will Smith",
        "anu,0,10,1,1,Keanu Reeves",
        "tar,0,10,1,1,Quentin Tarantino",
    })
    void Given_AValidTerm_When_CallsFindAll_Should_ReturnFiltered(
        final String expectedTerms,
        final int expectedPage,
        final int expectedPerPage,
        final int expectedItemsCount,
        final int expectedTotal,
        final String expectedName
    ) {
        mockMembers();

        final var expectedSort = "name";
        final var expectedDirection = "asc";

        final var aQuery = new SearchQuery(
                expectedPage,
                expectedPerPage,
                expectedTerms,
                expectedSort,
                expectedDirection);

        final var actualPage = this.castMemberMySQLGateway.findAll(aQuery);

        assertNotNull(actualPage);
        assertEquals(expectedPage, actualPage.currentPage());
        assertEquals(expectedPerPage, actualPage.perPage());
        assertEquals(expectedTotal, actualPage.total());
        assertEquals(expectedItemsCount, actualPage.items().size());
        assertEquals(expectedName, actualPage.items().get(0).getName());
    }

    @ParameterizedTest
    @CsvSource({
        "name,asc,0,10,5,5,Keanu Reeves",
        "name,desc,0,10,5,5,Will Smith",
        "createdAt,asc,0,10,5,5,Nicolas Cage",
        "createdAt,desc,0,10,5,5,Quentin Tarantino",
    })
    void Given_AValidSortAndDirection_When_CallsFindAll_Should_ReturnSorted(
        final String expectedSort,
        final String expectedDirection,
        final int expectedPage,
        final int expectedPerPage,
        final int expectedItemsCount,
        final int expectedTotal,
        final String expectedName
    ) {
        mockMembers();

        final var expectedTerms = "";

        final var aQuery = new SearchQuery(
                expectedPage,
                expectedPerPage,
                expectedTerms,
                expectedSort,
                expectedDirection);

        final var actualPage = this.castMemberMySQLGateway.findAll(aQuery);

        assertNotNull(actualPage);
        assertEquals(expectedPage, actualPage.currentPage());
        assertEquals(expectedPerPage, actualPage.perPage());
        assertEquals(expectedTotal, actualPage.total());
        assertEquals(expectedItemsCount, actualPage.items().size());
        assertEquals(expectedName, actualPage.items().get(0).getName());
    }

    @ParameterizedTest
    @CsvSource({
        "0,2,2,5,Keanu Reeves;Nicolas Cage",
        "1,2,2,5,Quentin Tarantino;Steven Spielberg",
        "2,2,1,5,Will Smith",
    })
    void Given_AValidPagination_When_CallsFindAll_Should_ReturnPaginated(
        final int expectedPage,
        final int expectedPerPage,
        final int expectedItemsCount,
        final int expectedTotal,
        final String expectedNames
    ) {
        mockMembers();

        final var expectedTerms = "";
        final String expectedSort = "name";
        final String expectedDirection = "asc";

        final var aQuery = new SearchQuery(
                expectedPage,
                expectedPerPage,
                expectedTerms,
                expectedSort,
                expectedDirection);

        final var actualPage = this.castMemberMySQLGateway.findAll(aQuery);

        assertNotNull(actualPage);
        assertEquals(expectedPage, actualPage.currentPage());
        assertEquals(expectedPerPage, actualPage.perPage());
        assertEquals(expectedTotal, actualPage.total());
        assertEquals(expectedItemsCount, actualPage.items().size());

        int index = 0;
        for (final String expectedName : expectedNames.split(";")) {
            assertEquals(expectedName, actualPage.items().get(index++).getName());
        }
    }

    private void mockMembers() {
        this.castMemberRepository.saveAllAndFlush(List.of(
            CastMemberJpaEntity.from(CastMember.newMember("Nicolas Cage", CastMemberType.ACTOR)),
            CastMemberJpaEntity.from(CastMember.newMember("Will Smith", CastMemberType.ACTOR)),
            CastMemberJpaEntity.from(CastMember.newMember("Steven Spielberg", CastMemberType.DIRECTOR)),
            CastMemberJpaEntity.from(CastMember.newMember("Keanu Reeves", CastMemberType.ACTOR)),
            CastMemberJpaEntity.from(CastMember.newMember("Quentin Tarantino", CastMemberType.DIRECTOR))
        ));
    }

}
